<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工工号" prop="empId">
        <el-input
          v-model="queryParams.empId"
          placeholder="请输入员工工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="年份" prop="year">
        <el-date-picker
          v-model="queryParams.year"
          type="year"
          placeholder="选择年份"
          value-format="yyyy"
          @change="handleYearChange"
        />
      </el-form-item>
      <el-form-item label="月份" prop="month">
        <el-select v-model="queryParams.month" placeholder="请选择月份" clearable>
          <el-option
            v-for="i in 12"
            :key="i"
            :label="i + '月'"
            :value="i"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['salary:monthly:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['salary:monthly:edit']"
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
          v-hasPermi="['salary:monthly:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-operation"
          size="mini"
          @click="handleGenerate"
          v-hasPermi="['salary:monthly:generate']"
        >批量生成工资条</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['salary:monthly:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="monthlyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="员工工号" align="center" prop="empId" />
      <el-table-column label="年月" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.row.year }}-{{ scope.row.month.toString().padStart(2, '0') }}
        </template>
      </el-table-column>
      <el-table-column label="基本工资" align="center" prop="baseSalary">
        <template slot-scope="scope">
          {{ scope.row.baseSalary }} 元
        </template>
      </el-table-column>
      <el-table-column label="出勤工资" align="center" prop="attendanceSalary">
        <template slot-scope="scope">
          {{ scope.row.attendanceSalary }} 元
        </template>
      </el-table-column>
      <el-table-column label="绩效工资" align="center" prop="performancePay">
        <template slot-scope="scope">
          {{ scope.row.performancePay }} 元
        </template>
      </el-table-column>
      <el-table-column label="出差补贴" align="center" prop="businessTripAllowance">
        <template slot-scope="scope">
          {{ scope.row.businessTripAllowance }} 元
        </template>
      </el-table-column>
      <el-table-column label="扣款" align="center" prop="deductionAmount">
        <template slot-scope="scope">
          {{ scope.row.deductionAmount }} 元
        </template>
      </el-table-column>
      <el-table-column label="加班费" align="center" prop="overtimePay">
        <template slot-scope="scope">
          {{ scope.row.overtimePay }} 元
        </template>
      </el-table-column>
      <el-table-column label="总工资" align="center" prop="totalSalary">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: bold;">{{ scope.row.totalSalary }} 元</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['salary:monthly:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['salary:monthly:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['salary:monthly:remove']"
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

    <!-- 添加或修改月度工资对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="员工工号" prop="empId">
              <el-input v-model="form.empId" placeholder="请输入员工工号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年月" required>
              <el-col :span="11">
                <el-form-item prop="year">
                  <el-date-picker
                    v-model="form.year"
                    type="year"
                    placeholder="选择年份"
                    value-format="yyyy"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col class="line" :span="2" style="text-align: center">-</el-col>
              <el-col :span="11">
                <el-form-item prop="month">
                  <el-select v-model="form.month" placeholder="请选择月份" style="width: 100%;">
                    <el-option
                      v-for="i in 12"
                      :key="i"
                      :label="i + '月'"
                      :value="i"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="基本工资" prop="baseSalary">
              <el-input-number v-model="form.baseSalary" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出勤工资" prop="attendanceSalary">
              <el-input-number v-model="form.attendanceSalary" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="绩效工资" prop="performancePay">
              <el-input-number v-model="form.performancePay" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出差补贴" prop="businessTripAllowance">
              <el-input-number v-model="form.businessTripAllowance" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="扣款金额" prop="deductionAmount">
              <el-input-number v-model="form.deductionAmount" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="加班费" prop="overtimePay">
              <el-input-number v-model="form.overtimePay" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总工资" prop="totalSalary">
              <el-input-number v-model="form.totalSalary" :precision="2" :min="0" :disabled="autoCalculate" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item>
              <el-checkbox v-model="autoCalculate" @change="handleAutoCalculate">自动计算总工资</el-checkbox>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="autoCalculate">
          <el-col :span="24">
            <div class="alert-info">
              <p><i class="el-icon-info"></i> 工资计算公式说明：</p>
              <p>1. 考勤工资 = 基本工资 ÷ 标准出勤天数(22天) × (实际出勤天数 – 缺勤天数)</p>
              <p>2. 异常扣款 = 迟到早退次数 × 固定扣款额(50元/次)</p>
              <p>3. 请假扣款 = 超额请假(超过2天) × 日工资 × 1.5</p>
              <p>4. 加班费 = 加班时长 × 时薪</p>
              <p>5. 出差补贴 = 出差天数 × 日薪 × 1.5</p>
              <p>6. 总工资 = 考勤工资 + 绩效工资 + 加班费 + 出差补贴 - 异常扣款 - 请假扣款</p>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="实际出勤" prop="actualAttendanceDays">
              <el-input-number v-model="form.actualAttendanceDays" :precision="0" :min="0" :max="31" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="迟早次数" prop="lateEarlyCount">
              <el-input-number v-model="form.lateEarlyCount" :precision="0" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缺勤天数" prop="absenceDays">
              <el-input-number v-model="form.absenceDays" :precision="0" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="请假天数" prop="leaveDays">
              <el-input-number v-model="form.leaveDays" :precision="0" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="加班小时" prop="overtimeHours">
              <el-input-number v-model="form.overtimeHours" :precision="1" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出差天数" prop="businessTripDays">
              <el-input-number v-model="form.businessTripDays" :precision="0" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量生成工资条对话框 -->
    <el-dialog :title="'批量生成工资条'" :visible.sync="generateOpen" width="500px" append-to-body>
      <el-form ref="generateForm" :model="generateParams" :rules="generateRules" label-width="80px">
        <el-form-item label="年份" prop="year">
          <el-date-picker
            v-model="generateParams.year"
            type="year"
            placeholder="选择年份"
            value-format="yyyy"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="generateParams.month" placeholder="请选择月份" style="width: 100%;">
            <el-option
              v-for="i in 12"
              :key="i"
              :label="i + '月'"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="generateParams.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGenerate" :loading="generateLoading">确 定</el-button>
        <el-button @click="cancelGenerate">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 工资详情对话框 -->
    <el-dialog title="工资详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="员工工号">{{ detailForm.empId }}</el-descriptions-item>
        <el-descriptions-item label="结算年月">{{ detailForm.year }}-{{ detailForm.month }}</el-descriptions-item>
        <el-descriptions-item label="基本工资">{{ detailForm.baseSalary }} 元</el-descriptions-item>
        <el-descriptions-item label="出勤工资">{{ detailForm.attendanceSalary }} 元</el-descriptions-item>
        <el-descriptions-item label="绩效工资">{{ detailForm.performancePay }} 元</el-descriptions-item>
        <el-descriptions-item label="出差补贴">{{ detailForm.businessTripAllowance }} 元</el-descriptions-item>
        <el-descriptions-item label="扣款金额">{{ detailForm.deductionAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="加班费">{{ detailForm.overtimePay }} 元</el-descriptions-item>
        <el-descriptions-item label="实际出勤">{{ detailForm.actualAttendanceDays }} 天</el-descriptions-item>
        <el-descriptions-item label="迟早次数">{{ detailForm.lateEarlyCount }} 次</el-descriptions-item>
        <el-descriptions-item label="缺勤天数">{{ detailForm.absenceDays }} 天</el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ detailForm.leaveDays }} 天</el-descriptions-item>
        <el-descriptions-item label="加班小时">{{ detailForm.overtimeHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="出差天数">{{ detailForm.businessTripDays }} 天</el-descriptions-item>
        <el-descriptions-item label="总工资" :span="2">
          <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">{{ detailForm.totalSalary }} 元</span>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSalaryMonthly, getSalaryMonthly, delSalaryMonthly, addSalaryMonthly, updateSalaryMonthly, generateMonthlySalary } from "@/api/salary/monthly";
import { getSalaryBaseInfoByEmpId } from "@/api/salary/baseInfo";

export default {
  name: "SalaryMonthly",
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
      // 月度工资记录表格数据
      monthlyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 自动计算总工资
      autoCalculate: true,
      // 批量生成工资条弹窗
      generateOpen: false,
      // 批量生成工资条参数
      generateParams: {
        year: null,
        month: null,
        remark: null
      },
      // 生成按钮加载状态
      generateLoading: false,
      // 批量生成验证规则
      generateRules: {
        year: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        month: [
          { required: true, message: "月份不能为空", trigger: "blur" }
        ]
      },
      // 工资详情弹窗
      detailOpen: false,
      // 工资详情数据
      detailForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        empId: null,
        year: null,
        month: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        empId: [
          { required: true, message: "员工工号不能为空", trigger: "blur" }
        ],
        year: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        month: [
          { required: true, message: "月份不能为空", trigger: "blur" }
        ],
        baseSalary: [
          { required: true, message: "基本工资不能为空", trigger: "blur" }
        ],
        totalSalary: [
          { required: true, message: "总工资不能为空", trigger: "blur" }
        ]
      },
      // 员工薪资基本配置
      empSalaryConfig: {
        standardAttendanceDays: 22, // 默认值
        lateDeduction: 50, // 默认值
        absenceDeductionRatio: 1.5, // 默认值
        overtimeRatio: 1.5, // 默认值
        businessTripRatio: 1.5, // 默认值
      },
      // 防止重复调用API
      isLoadingConfig: false,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询月度工资记录列表 */
    getList() {
      this.loading = true;
      listSalaryMonthly(this.queryParams).then(response => {
        this.monthlyList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        console.error("获取月度工资记录失败", error);
        this.$modal.msgError("获取月度工资记录失败");
        this.loading = false;
      });
    },
    // 年份选择变化
    handleYearChange(year) {
      // 可以在这里添加其他逻辑，例如重置月份等
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        empId: "",
        year: new Date().getFullYear().toString(),
        month: new Date().getMonth() + 1,
        baseSalary: 0,
        attendanceSalary: 0,
        performancePay: 0,
        businessTripAllowance: 0,
        deductionAmount: 0,
        overtimePay: 0,
        totalSalary: 0,
        actualAttendanceDays: 0,
        lateEarlyCount: 0,
        absenceDays: 0,
        leaveDays: 0,
        overtimeHours: 0,
        businessTripDays: 0,
        remark: ""
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加月度工资记录（同一员工在同一年月只能有一条记录）";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getSalaryMonthly(id).then(response => {
        this.form = response.data;
        // 不在这里调用fetchEmpSalaryConfig，依靠watch监听来调用
        this.open = true;
        this.title = "修改月度工资记录（不能修改为已存在记录的员工和年月）";
      }).catch(error => {
        console.error("获取月度工资记录详情失败", error);
        this.$modal.msgError("获取月度工资记录详情失败");
      });
    },
    /** 工资详情按钮操作 */
    handleDetail(row) {
      getSalaryMonthly(row.id).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      }).catch(error => {
        console.error("获取月度工资记录详情失败", error);
        this.$modal.msgError("获取月度工资记录详情失败");
      });
    },
    /** 自动计算总工资 */
    handleAutoCalculate() {
      if (this.autoCalculate) {
        this.calculateTotalSalary();
      }
    },
    /** 计算总工资 */
    calculateTotalSalary() {
      if (!this.autoCalculate) {
        return;
      }

      // 获取基本参数
      const baseSalary = parseFloat(this.form.baseSalary) || 0;
      const standardDays = this.empSalaryConfig.standardAttendanceDays || 22;
      const actualDays = parseFloat(this.form.actualAttendanceDays) || 0;
      const absenceDays = parseFloat(this.form.absenceDays) || 0;
      
      // 1. 计算考勤工资：基本工资÷标准出勤天数×（实际出勤天数–缺勤天数）
      // 优先使用配置中的日工资，如果没有则计算
      let dailySalary = this.empSalaryConfig.daySalary || (standardDays > 0 ? baseSalary / standardDays : 0);
      // 优先使用配置中的小时工资，如果没有则计算
      let hourSalary = this.empSalaryConfig.hourSalary || (dailySalary / 8);

      // 计算考勤工资
      const attendanceSalary = dailySalary * (actualDays - absenceDays);
      this.form.attendanceSalary = parseFloat(attendanceSalary.toFixed(2));
      
      // 2. 异常扣款：迟到早退次数×固定扣款
      const lateEarlyCount = parseInt(this.form.lateEarlyCount) || 0;
      const lateDeduction = this.empSalaryConfig.lateDeduction || 50; // 从配置获取
      const deductionAmount = lateEarlyCount * lateDeduction;
      this.form.deductionAmount = parseFloat(deductionAmount.toFixed(2));
      
      // 3. 请假扣款：超额请假×日工资×缺勤扣款比例
      const leaveDays = parseFloat(this.form.leaveDays) || 0;
      const allowedLeaveDays = 2; // 假设每月允许请假2天不扣款
      const absenceDeductionRatio = this.empSalaryConfig.absenceDeductionRatio || 1.5; // 从配置获取
      const excessLeaveDays = Math.max(0, leaveDays - allowedLeaveDays);
      const leaveDeduction = dailySalary * excessLeaveDays * absenceDeductionRatio;
      this.form.leaveDeduction = parseFloat(leaveDeduction.toFixed(2));
      
      // 4. 加班费：加班小时×小时工资×加班比例
      const overtimeHours = parseFloat(this.form.overtimeHours) || 0;
      const overtimeRatio = this.empSalaryConfig.overtimeRatio || 1.5; // 从配置获取
      const overtimePay = hourSalary * overtimeHours * overtimeRatio;
      this.form.overtimePay = parseFloat(overtimePay.toFixed(2));
      
      // 5. 出差补贴：出差天数×日工资×出差补贴比例
      const businessTripDays = parseFloat(this.form.businessTripDays) || 0;
      const businessTripRatio = this.empSalaryConfig.businessTripRatio || 1.5; // 从配置获取
      const tripAllowance = dailySalary * businessTripDays * businessTripRatio;
      this.form.businessTripAllowance = parseFloat(tripAllowance.toFixed(2));
      
      // 6. 最终计算总工资
      const performancePay = parseFloat(this.form.performancePay) || 0; // 绩效工资
      
      const totalSalary = 
        attendanceSalary + 
        performancePay + 
        overtimePay + 
        tripAllowance - 
        deductionAmount - 
        leaveDeduction;
      
      this.form.totalSalary = parseFloat(totalSalary.toFixed(2));
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 在提交前重新计算所有薪资项目
          if (this.autoCalculate) {
            // 先更新考勤相关字段
            this.calculateFields();
            // 再计算总工资
            this.calculateTotalSalary();
          }
          
          if (this.form.id != null) {
            updateSalaryMonthly(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("修改失败:", error);
            });
          } else {
            addSalaryMonthly(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("新增失败:", error);
            });
          }
        }
      });
    },
    // 计算所有考勤相关字段，确保数据一致性
    calculateFields() {
      // 确保所有必要的字段都有值
      this.form.actualAttendanceDays = this.form.actualAttendanceDays || 0;
      this.form.absenceDays = this.form.absenceDays || 0;
      this.form.lateEarlyCount = this.form.lateEarlyCount || 0;
      this.form.leaveDays = this.form.leaveDays || 0;
      this.form.overtimeHours = this.form.overtimeHours || 0;
      this.form.businessTripDays = this.form.businessTripDays || 0;
      
      // 调用计算总工资方法会自动计算所有薪资项目
      this.calculateTotalSalary();
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除月度工资记录编号为"' + ids + '"的数据项？').then(() => {
        return delSalaryMonthly(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 批量生成工资条按钮操作 */
    handleGenerate() {
      this.generateParams = {
        year: new Date().getFullYear().toString(),
        month: new Date().getMonth() + 1,
        remark: `${new Date().getFullYear()}年${new Date().getMonth() + 1}月工资`
      };
      this.generateOpen = true;
    },
    // 取消批量生成
    cancelGenerate() {
      this.generateOpen = false;
      this.generateParams = {
        year: null,
        month: null,
        remark: null
      };
    },
    // 提交批量生成
    submitGenerate() {
      this.$refs["generateForm"].validate(valid => {
        if (valid) {
          this.generateLoading = true;
          generateMonthlySalary(this.generateParams.year, this.generateParams.month).then(response => {
            // 显示后端返回的成功消息
            this.$modal.msgSuccess(response.msg || "批量生成工资条成功");
            this.generateOpen = false;
            this.getList();
            this.generateLoading = false;
          }).catch(error => {
            console.log("生成工资条结果:", error);
            
            // 如果错误被标记为信息类消息，则已经由全局处理显示了消息，这里只需关闭窗口
            if (error.isInfoMessage) {
              this.generateOpen = false;
              this.generateLoading = false;
              return;
            }
            
            // 其他情况仍然在本地处理
            const errorMsg = error.response && error.response.data && error.response.data.msg 
              ? error.response.data.msg 
              : "批量生成工资条失败";
              
            // 显示错误消息
            this.$modal.msgError(errorMsg);
            this.generateLoading = false;
          }).finally(() => {
            // 无论成功或失败，都刷新列表
            this.getList();
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('salary/monthly/export', {
        ...this.queryParams
      }, `月度工资记录_${new Date().getTime()}.xlsx`);
    },
    // 获取员工薪资基本配置
    fetchEmpSalaryConfig(empId) {
      if (!empId || this.isLoadingConfig) return;
      
      this.isLoadingConfig = true;
      console.log("正在获取员工ID为", empId, "的薪资基本配置");
      
      // 确保使用正确的API路径格式
      getSalaryBaseInfoByEmpId(empId).then(response => {
        if (response.data) {
          const baseInfo = response.data;
          this.empSalaryConfig = {
            standardAttendanceDays: baseInfo.standardAttendanceDays || 22,
            lateDeduction: baseInfo.lateDeduction || 50,
            absenceDeductionRatio: baseInfo.absenceDeductionRatio || 1.5,
            overtimeRatio: baseInfo.overtimeRatio || 1.5,
            businessTripRatio: baseInfo.businessTripRatio || 1.5,
            daySalary: baseInfo.daySalary,
            hourSalary: baseInfo.hourSalary
          };
          
          console.log("成功获取到员工薪资基本配置:", this.empSalaryConfig);
          // 更新完配置后重新计算工资
          this.calculateTotalSalary();
        }
      }).catch(error => {
        console.error("获取员工薪资基本配置失败", error);
        this.$modal.msgWarning("未找到该员工的薪资基本配置，将使用默认配置");
      }).finally(() => {
        this.isLoadingConfig = false;
      });
    },
    // 员工ID变更时，获取该员工的薪资基本配置
    handleEmpIdChange(empId) {
      this.fetchEmpSalaryConfig(empId);
    },
  },
  watch: {
    // 监听员工ID变更
    "form.empId": function(val) {
      if (val) {
        this.handleEmpIdChange(val);
      }
    },
    // 监听薪资相关字段变更
    "form.baseSalary": function() { this.calculateTotalSalary(); },
    "form.actualAttendanceDays": function() { this.calculateTotalSalary(); },
    "form.absenceDays": function() { this.calculateTotalSalary(); },
    "form.lateEarlyCount": function() { this.calculateTotalSalary(); },
    "form.leaveDays": function() { this.calculateTotalSalary(); },
    "form.overtimeHours": function() { this.calculateTotalSalary(); },
    "form.businessTripDays": function() { this.calculateTotalSalary(); },
    "form.performancePay": function() { this.calculateTotalSalary(); }
  }
};
</script>

<style scoped>
.alert-info {
  background-color: #f0f9ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  padding: 10px 15px;
  margin-bottom: 15px;
  color: #606266;
}

.alert-info p {
  margin: 5px 0;
  line-height: 1.5;
}

.alert-info p:first-child {
  font-weight: bold;
  color: #409eff;
}

.alert-info i {
  margin-right: 5px;
}
</style> 