<template>
  <div class="app-container home">
    <div style="font-weight: bold; font-size: 24px; text-align: center;">
      人事考勤管理系统后台
    </div>
    <el-divider />

    <!-- KPI卡片指标区域 -->
    <el-row :gutter="20" class="kpi-dashboard">
      <el-col :xs="12" :sm="6" :md="6" :lg="4" v-for="(item, index) in kpiData" :key="index">
        <el-card shadow="hover" :class="[`kpi-card`, `kpi-card-${index%4}`]">
          <div class="kpi-card-content">
            <div class="kpi-icon">
              <i :class="item.icon"></i>
            </div>
            <div class="kpi-info">
              <div class="kpi-number">{{ item.value }}</div>
              <div class="kpi-title">{{ item.title }}</div>
            </div>
          </div>
          <div class="kpi-footer" v-if="item.change !== undefined">
            <i :class="item.change >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            <span>比昨日{{ item.change >= 0 ? '增加' : '减少' }}{{ Math.abs(item.change) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>考勤统计</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              unlink-panels
              style="float: right;"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="pickerOptions"
              @change="handleChange"
            />
          </div>
          <div id="statisticsChart" style="width: 100%; height: 300px" />
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>考勤规则</span>
          </div>
          <div class="attendance-rules">
            <ul>
              <li><i class="el-icon-time"></i> 上班考勤开始时间: {{ rules.attendance_start_time || '06:00' }}</li>
              <li><i class="el-icon-time"></i> 上班时间: {{ rules.attendance_time || '08:30' }}</li>
              <li><i class="el-icon-time"></i> 上班考勤截止时间: {{ rules.attendance_end_time || '09:30' }}</li>
              <li><i class="el-icon-time"></i> 下班考勤开始时间: {{ rules.closing_start_time || '16:30' }}</li>
              <li><i class="el-icon-time"></i> 下班时间: {{ rules.closing_time || '18:30' }}</li>
              <li><i class="el-icon-time"></i> 下班考勤截止时间: {{ rules.closing_end_time || '23:59' }}</li>
              <li><i class="el-icon-warning"></i> 迟到: 上班时间后至上班考勤截止时间前打卡</li>
              <li><i class="el-icon-warning"></i> 早退: 下班考勤开始时间后至下班时间前打卡</li>
              <li><i class="el-icon-warning-outline"></i> 旷工: 未打卡或超过上班考勤截止时间打卡</li>
              <li>
                <div @click="showHolidays = !showHolidays" style="cursor:pointer">
                  <i class="el-icon-date"></i> 法定节假日 
                  <i :class="showHolidays ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" style="float:right"></i>
                </div>
                <div v-if="showHolidays" style="margin: 10px 0; padding-left: 20px">
                  <el-tag v-for="(date, index) in holidayList" :key="'holiday-'+index" size="mini" type="danger" style="margin: 2px">
                    {{ date }}
                  </el-tag>
                  <div v-if="holidayList.length === 0" style="color: #909399; font-size: 12px">暂无节假日数据</div>
                </div>
              </li>
              <li>
                <div @click="showWorkdays = !showWorkdays" style="cursor:pointer">
                  <i class="el-icon-date"></i> 节假日调休上班日
                  <i :class="showWorkdays ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" style="float:right"></i>
                </div>
                <div v-if="showWorkdays" style="margin: 10px 0; padding-left: 20px">
                  <el-tag v-for="(date, index) in workdayList" :key="'workday-'+index" size="mini" type="warning" style="margin: 2px">
                    {{ date }}
                  </el-tag>
                  <div v-if="workdayList.length === 0" style="color: #909399; font-size: 12px">暂无调休上班日数据</div>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>未打卡员工列表</span>
            <el-button 
              style="float: right; padding: 3px 0" 
              type="text"
              icon="el-icon-refresh"
              @click="getNotCheckedInEmployees"
            >刷新</el-button>
          </div>
          <div class="not-checkin-employees">
            <div v-if="loading" class="loading-container">
              <i class="el-icon-loading"></i>
              <span>加载中...</span>
            </div>
            <div v-else-if="notCheckedInEmployees.length === 0" class="empty-message">
              <i class="el-icon-circle-check"></i>
              <p>所有员工均已打卡</p>
            </div>
            <el-table v-else :data="notCheckedInEmployees" style="width: 100%" size="small" border>
              <el-table-column prop="empInfoName" label="姓名" width="80" />
              <el-table-column prop="empInfoId" label="工号" width="100" />
              <el-table-column prop="deptName" label="部门" />
            </el-table>
            
            <div class="pagination-container" v-if="notCheckedInEmployees.length > 0">
              <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="queryParams.pageNum"
                :page-sizes="[5, 10, 20, 30]"
                :page-size="queryParams.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
              </el-pagination>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listAttendance } from "@/api/attendance/attendance";
import { getDashboardData, getAttendanceStatistics } from "@/api/system/dashboard";
import { parseTime } from "@/utils/ruoyi";
import { getNotCheckedInEmployees } from "@/api/system/index";

export default {
  name: "Index",
  data() {
    return {
      // 考勤统计日期范围
      dateRange: [],
      // 日期选择器快捷选项
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }
        ]
      },
      // 统计图表实例
      chartInstance: null,
      // 未打卡员工列表
      notCheckedInEmployees: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 分页总条数
      total: 0,
      // KPI指标数据
      kpiData: [
        { title: '今日到勤', value: 0, icon: 'el-icon-user', change: 0 },
        { title: '请假中人数', value: 0, icon: 'el-icon-time', change: 0 },
        { title: '今日迟到', value: 0, icon: 'el-icon-warning', change: 0 },
        { title: '今日早退', value: 0, icon: 'el-icon-alarm-clock', change: 0 },
        { title: '本月出差人数', value: 0, icon: 'el-icon-suitcase', change: 0 },
        { title: '在职人数', value: 0, icon: 'el-icon-user-solid', change: 0 }
      ],
      // 系统消息
      messages: [],
      // 考勤规则
      rules: {},
      // 显示节假日列表
      showHolidays: false,
      // 显示调休上班日列表
      showWorkdays: false,
      // 节假日列表
      holidayList: [],
      // 调休上班日列表
      workdayList: [],
      loading: false
    };
  },
  created() {
    // 设置默认日期范围为最近一周
    const end = new Date();
    const start = new Date();
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
    this.dateRange = [this.formatDate(start), this.formatDate(end)];
  },
  mounted() {
    this.initChart();
    this.getKpiData();
    this.getNotCheckedInEmployees();
  },
  beforeDestroy() {
    if (this.statisticsChart) {
      this.statisticsChart.dispose();
      this.statisticsChart = null;
    }
  },
  methods: {
    // 格式化时间
    parseTime,
    
    // 跳转到指定链接
    goTarget(href) {
      window.open(href, "_blank");
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 初始化图表
    initChart() {
      this.statisticsChart = echarts.init(document.getElementById('statisticsChart'));
      this.getAttendanceStatistics();
    },
    
    // 获取KPI数据
    getKpiData() {
      this.loading = true;
      getDashboardData().then(response => {
        const data = response.data;
        
        // 更新KPI数据
        if (data.attendance) {
          this.kpiData[0].value = data.attendance.value;
          this.kpiData[0].change = data.attendance.change;
        }
        
        if (data.leave) {
          this.kpiData[1].value = data.leave.value;
          this.kpiData[1].change = data.leave.change;
        }
        
        if (data.late) {
          this.kpiData[2].value = data.late.value;
          this.kpiData[2].change = data.late.change;
        }
        
        if (data.early) {
          this.kpiData[3].value = data.early.value;
          this.kpiData[3].change = data.early.change;
        }
        
        if (data.businessTrip) {
          this.kpiData[4].value = data.businessTrip.value;
          this.kpiData[4].change = data.businessTrip.change;
        }
        
        if (data.employee) {
          this.kpiData[5].value = data.employee.value;
          this.kpiData[5].change = data.employee.change;
        }
        
        // 更新系统消息
        if (data.messages) {
          this.messages = data.messages;
        }

        // 更新考勤规则
        if (data.attendanceRules) {
          this.rules = data.attendanceRules;
          
          // 处理节假日列表
          try {
            if (this.rules.holiday_list) {
              this.holidayList = JSON.parse(this.rules.holiday_list);
            } else {
              this.holidayList = [];
            }
          } catch (e) {
            console.error('解析节假日列表出错', e);
            this.holidayList = [];
          }
          
          // 处理调休上班日列表
          try {
            if (this.rules.workday_list) {
              this.workdayList = JSON.parse(this.rules.workday_list);
            } else {
              this.workdayList = [];
            }
          } catch (e) {
            console.error('解析调休上班日列表出错', e);
            this.workdayList = [];
          }
        }
        
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 获取考勤统计数据
    getAttendanceStatistics() {
      if (!this.dateRange || this.dateRange.length !== 2) {
        return;
      }
      
      const startDate = this.dateRange[0];
      const endDate = this.dateRange[1];
      
      // 使用新的后端专用统计接口
      getAttendanceStatistics(startDate, endDate).then(response => {
        const data = response.data || {};
        
        // 准备图表数据
        const dates = data.dates || [];
        const normalData = data.normalData || [];
        const lateData = data.lateData || [];
        const earlyData = data.earlyData || [];
        const absentData = data.absentData || [];
        
        // 设置图表选项
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['正常', '迟到', '早退', '旷工']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: dates,
            axisLabel: {
              rotate: 45
            }
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '正常',
              type: 'bar',
              stack: '总量',
              emphasis: {
                focus: 'series'
              },
              data: normalData,
              itemStyle: {
                color: '#67C23A'
              }
            },
            {
              name: '迟到',
              type: 'bar',
              stack: '总量',
              emphasis: {
                focus: 'series'
              },
              data: lateData,
              itemStyle: {
                color: '#E6A23C'
              }
            },
            {
              name: '早退',
              type: 'bar',
              stack: '总量',
              emphasis: {
                focus: 'series'
              },
              data: earlyData,
              itemStyle: {
                color: '#909399'
              }
            },
            {
              name: '旷工',
              type: 'bar',
              stack: '总量',
              emphasis: {
                focus: 'series'
              },
              data: absentData,
              itemStyle: {
                color: '#F56C6C'
              }
            }
          ]
        };
        
        // 渲染图表
        this.statisticsChart.setOption(option);
      }).catch(error => {
        console.error("获取考勤统计数据失败", error);
      });
    },
    
    // 日期选择变化
    handleChange() {
      this.getAttendanceStatistics();
    },
    
    // 获取未打卡员工列表
    getNotCheckedInEmployees() {
      this.loading = true;
      getNotCheckedInEmployees(this.queryParams).then(response => {
        this.notCheckedInEmployees = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 分页每页条数改变
    handleSizeChange(size) {
      this.queryParams.pageSize = size;
      this.getNotCheckedInEmployees();
    },
    
    // 分页页码改变
    handleCurrentChange(page) {
      this.queryParams.pageNum = page;
      this.getNotCheckedInEmployees();
    }
  }
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
  
  .kpi-dashboard {
    margin-bottom: 20px;
  }
  
  .kpi-card {
    height: 110px;
    margin-bottom: 20px;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
    
    .kpi-card-content {
      display: flex;
      align-items: center;
    }
    
    .kpi-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      
      i {
        font-size: 28px;
        color: #fff;
      }
    }
    
    .kpi-info {
      flex: 1;
    }
    
    .kpi-number {
      font-size: 24px;
      font-weight: bold;
      line-height: 1.2;
    }
    
    .kpi-title {
      font-size: 14px;
      color: #909399;
      margin-top: 5px;
    }
    
    .kpi-footer {
      margin-top: 10px;
      font-size: 12px;
      color: #909399;
      
      i {
        margin-right: 5px;
      }
    }
  }
  
  .kpi-card-0 {
    .kpi-icon {
      background-color: rgba(103, 194, 58, 0.1);
      i {
        color: #67C23A;
      }
    }
    .kpi-number {
      color: #67C23A;
    }
  }
  
  .kpi-card-1 {
    .kpi-icon {
      background-color: rgba(230, 162, 60, 0.1);
      i {
        color: #E6A23C;
      }
    }
    .kpi-number {
      color: #E6A23C;
    }
  }
  
  .kpi-card-2 {
    .kpi-icon {
      background-color: rgba(245, 108, 108, 0.1);
      i {
        color: #F56C6C;
      }
    }
    .kpi-number {
      color: #F56C6C;
    }
  }
  
  .kpi-card-3 {
    .kpi-icon {
      background-color: rgba(64, 158, 255, 0.1);
      i {
        color: #409EFF;
      }
    }
    .kpi-number {
      color: #409EFF;
    }
  }
  
  .attendance-rules {
    ul {
      list-style-type: none;
      padding: 0;
      
      li {
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        i {
          margin-right: 10px;
          color: #409EFF;
        }
      }
    }
  }
  
  .system-messages {
    .empty-message {
      text-align: center;
      padding: 30px 0;
      color: #909399;
      
      i {
        font-size: 40px;
        margin-bottom: 10px;
      }
    }
    
    .message-item {
      padding: 10px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .message-title {
        font-weight: bold;
        margin-bottom: 5px;
        
        i {
          margin-right: 5px;
          color: #409EFF;
        }
      }
      
      .message-content {
        color: #606266;
        font-size: 12px;
        margin-bottom: 5px;
      }
      
      .message-time {
        color: #909399;
        font-size: 12px;
        text-align: right;
      }
    }
  }
}
</style>

