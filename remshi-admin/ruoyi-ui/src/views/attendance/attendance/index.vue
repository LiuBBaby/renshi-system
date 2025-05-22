<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工姓名" prop="attendanceInfoName">
        <el-input
          v-model="queryParams.attendanceInfoName"
          placeholder="请输入员工姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工工号" prop="empId">
        <el-input
          v-model="queryParams.empId"
          placeholder="请输入员工工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上班打卡状态" prop="checkInStatus">
        <el-select v-model="queryParams.checkInStatus" placeholder="请选择上班打卡状态" clearable>
          <el-option
            v-for="dict in checkInStatusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="下班打卡状态" prop="checkOutStatus">
        <el-select v-model="queryParams.checkOutStatus" placeholder="请选择下班打卡状态" clearable>
          <el-option
            v-for="dict in checkOutStatusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="考勤日期">
        <el-date-picker
          v-model="daterangeAttendanceInfoDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="员工评价" prop="evaluate">
        <el-select v-model="queryParams.evaluate" placeholder="请选择员工评价" clearable>
          <el-option
            v-for="dict in dict.type.evaluate"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['attendance:attendance:add']"
        >新增</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['attendance:attendance:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['attendance:attendance:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['attendance:attendance:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="attendanceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="attendanceInfoId" />
      <el-table-column label="员工姓名" align="center" prop="attendanceInfoName" />
      <el-table-column label="员工工号" align="center" prop="empId" />
      <el-table-column label="上班时间" align="center" prop="attendanceInfoGo" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.attendanceInfoGo, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下班时间" align="center" prop="attendanceInfoOut" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.attendanceInfoOut, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上班打卡状态" align="center" prop="checkInStatus">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.checkInStatus === 0">正常</el-tag>
          <el-tag type="warning" v-else-if="scope.row.checkInStatus === 1">迟到</el-tag>
          <el-tag type="info" v-else-if="scope.row.checkInStatus === 2">未打卡</el-tag>
          <el-tag v-else>{{ scope.row.checkInStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下班打卡状态" align="center" prop="checkOutStatus">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.checkOutStatus === 0">正常</el-tag>
          <el-tag type="warning" v-else-if="scope.row.checkOutStatus === 1">早退</el-tag>
          <el-tag type="info" v-else-if="scope.row.checkOutStatus === 2">未打卡</el-tag>
          <el-tag v-else>{{ scope.row.checkOutStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="考勤日期" align="center" prop="attendanceInfoDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.attendanceInfoDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="员工评价" align="center" prop="evaluate">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.evaluate" :value="scope.row.evaluate"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['attendance:attendance:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['attendance:attendance:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改考勤管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="员工姓名" prop="attendanceInfoName">
          <el-input v-model="form.attendanceInfoName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="员工工号" prop="empId">
          <el-input v-model="form.empId" placeholder="请输入员工工号" />
        </el-form-item>
        <el-form-item label="上班时间" prop="attendanceInfoGo">
          <el-date-picker clearable
                          v-model="form.attendanceInfoGo"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择上班时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="下班时间" prop="attendanceInfoOut">
          <el-date-picker clearable
                          v-model="form.attendanceInfoOut"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择下班时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="上班打卡状态" prop="checkInStatus">
          <el-select v-model="form.checkInStatus" placeholder="请选择上班打卡状态">
            <el-option
              v-for="dict in checkInStatusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下班打卡状态" prop="checkOutStatus">
          <el-select v-model="form.checkOutStatus" placeholder="请选择下班打卡状态">
            <el-option
              v-for="dict in checkOutStatusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考勤日期" prop="attendanceInfoDate">
          <el-date-picker clearable
                          v-model="form.attendanceInfoDate"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择考勤日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="员工评价" prop="evaluate">
          <el-select v-model="form.evaluate" placeholder="请选择员工评价">
            <el-option
              v-for="dict in dict.type.evaluate"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAttendance, getAttendance, delAttendance, addAttendance, updateAttendance } from "@/api/attendance/attendance";

export default {
  name: "Attendance",
  dicts: ['evaluate', 'attendance_info_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 考勤管理表格数据
      attendanceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 员工评价时间范围
      daterangeAttendanceInfoDate: [],
      // 上班打卡状态选项
      checkInStatusOptions: [
        { value: 0, label: '正常' },
        { value: 1, label: '迟到' },
        { value: 2, label: '未打卡' }
      ],
      // 下班打卡状态选项
      checkOutStatusOptions: [
        { value: 0, label: '正常' },
        { value: 1, label: '早退' },
        { value: 2, label: '未打卡' }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        attendanceInfoName: null,
        empId: null,
        checkInStatus: null,
        checkOutStatus: null,
        attendanceInfoDate: null,
        evaluate: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询考勤管理列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeAttendanceInfoDate && '' != this.daterangeAttendanceInfoDate) {
        this.queryParams.params["beginAttendanceInfoDate"] = this.daterangeAttendanceInfoDate[0];
        this.queryParams.params["endAttendanceInfoDate"] = this.daterangeAttendanceInfoDate[1];
      }
      listAttendance(this.queryParams).then(response => {
        this.attendanceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        attendanceInfoId: null,
        attendanceInfoName: null,
        empId: null,
        attendanceInfoGo: null,
        attendanceInfoOut: null,
        checkInStatus: 0,  // 默认为正常
        checkOutStatus: 0,  // 默认为正常
        attendanceInfoDate: null,
        createTime: null,
        evaluate: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeAttendanceInfoDate = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.attendanceInfoId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加考勤管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const attendanceInfoId = row.attendanceInfoId || this.ids
      getAttendance(attendanceInfoId).then(response => {
        this.form = response.data;
        // 确保状态值是数字类型
        if(this.form.checkInStatus !== null) {
          this.form.checkInStatus = Number(this.form.checkInStatus);
        }
        if(this.form.checkOutStatus !== null) {
          this.form.checkOutStatus = Number(this.form.checkOutStatus);
        }
        this.open = true;
        this.title = "修改考勤管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 确保状态是数字类型
          this.form.checkInStatus = Number(this.form.checkInStatus);
          this.form.checkOutStatus = Number(this.form.checkOutStatus);
          
          // 删除attendanceInfoStatus字段，防止覆盖checkInStatus和checkOutStatus
          if (this.form.attendanceInfoStatus !== undefined) {
            delete this.form.attendanceInfoStatus;
          }
          
          if (this.form.attendanceInfoId != null) {
            updateAttendance(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAttendance(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const attendanceInfoIds = row.attendanceInfoId || this.ids;
      this.$modal.confirm('是否确认删除考勤管理编号为"' + attendanceInfoIds + '"的数据项？').then(function() {
        return delAttendance(attendanceInfoIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('attendance/attendance/export', {
        ...this.queryParams
      }, `attendance_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
