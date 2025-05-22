<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工ID" prop="empId">
        <el-input
          v-model="queryParams.empId"
          placeholder="请输入员工ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工姓名" prop="empName">
        <el-input
          v-model="queryParams.empName"
          placeholder="请输入员工姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="年份" prop="year">
        <el-input
          v-model="queryParams.year"
          placeholder="请输入年份，如2025"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="月份" prop="month">
        <el-select v-model="queryParams.month" placeholder="请选择月份" clearable>
          <el-option
            v-for="month in 12"
            :key="month"
            :label="month + '月'"
            :value="month"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="monthlyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" width="50" />
      <el-table-column label="员工ID" align="center" prop="empId" />
      <el-table-column label="员工姓名" align="center" prop="empName" />
      <el-table-column label="部门名称" align="center" prop="deptName" />
      <el-table-column label="年份" align="center" prop="year" />
      <el-table-column label="月份" align="center" prop="month" />
      <el-table-column label="应出勤天数" align="center" prop="workDays" />
      <el-table-column label="实际出勤天数" align="center" prop="actualWorkDays" />
      <el-table-column label="请假天数" align="center" prop="leaveDays" />
      <el-table-column label="迟到次数" align="center" prop="lateTimes" />
      <el-table-column label="早退次数" align="center" prop="earlyTimes" />
      <el-table-column label="出差天数" align="center" prop="businessTripDays" />
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改月度考勤对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="员工ID" prop="empId">
          <el-input v-model="form.empId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="empName">
          <el-input v-model="form.empName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-input
            v-model="form.year"
            placeholder="请输入年份，如2025"
            clearable
          />
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="form.month" placeholder="请选择月份">
            <el-option
              v-for="month in 12"
              :key="month"
              :label="month + '月'"
              :value="month"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="应出勤天数" prop="workDays">
          <el-input-number v-model="form.workDays" :min="0" :max="31" />
        </el-form-item>
        <el-form-item label="实际出勤天数" prop="actualWorkDays">
          <el-input-number v-model="form.actualWorkDays" :min="0" :max="31" />
        </el-form-item>
        <el-form-item label="请假天数" prop="leaveDays">
          <el-input-number v-model="form.leaveDays" :min="0" :max="31" />
        </el-form-item>
        <el-form-item label="迟到次数" prop="lateTimes">
          <el-input-number v-model="form.lateTimes" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="早退次数" prop="earlyTimes">
          <el-input-number v-model="form.earlyTimes" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="出差天数" prop="businessTripDays">
          <el-input-number v-model="form.businessTripDays" :min="0" :max="31" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { listAttendanceMonthly, updateAttendanceMonthly } from '@/api/attendance/attendanceMonthly'

export default {
  name: 'AttendanceMonthly',
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
      // 月度考勤表格数据
      monthlyList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        empId: null,
        empName: null,
        deptName: null,
        year: null,
        month: null,
        params: {}
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        empId: [
          { required: true, message: '员工ID不能为空', trigger: 'blur' }
        ],
        empName: [
          { required: true, message: '员工姓名不能为空', trigger: 'blur' }
        ],
        deptId: [
          { required: true, message: '部门ID不能为空', trigger: 'blur' }
        ],
        deptName: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' }
        ],
        year: [
          { required: true, message: '年份不能为空', trigger: 'blur' }
        ],
        month: [
          { required: true, message: '月份不能为空', trigger: 'blur' }
        ],
        workDays: [
          { required: true, message: '应出勤天数不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询月度考勤列表 */
    getList() {
      this.loading = true
      listAttendanceMonthly(this.queryParams).then(response => {
        this.monthlyList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(error => {
        console.error('Error calling listAttendanceMonthly API:', error)
        this.$modal.msgError('获取月度考勤列表失败，请检查网络或权限')
        this.loading = false
      })
    },
    /** 年份选择变化 */
    handleYearChange(val) {
      if (val) {
        this.queryParams.year = parseInt(val)
      }
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        empId: null,
        empName: null,
        deptId: null,
        deptName: null,
        year: null,
        month: null,
        workDays: null,
        actualWorkDays: null,
        absentDays: null,
        leaveDays: null,
        lateTimes: null,
        earlyTimes: null,
        overtimeHours: null,
        businessTripDays: null,
        remark: null,
        createTime: null
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      // 创建新的查询对象，只包含分页信息
      const query = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        params: {}
      }
      
      // 所有筛选条件都通过params传递
      if (this.queryParams.empId) {
        query.params.empId = this.queryParams.empId
      }
      
      if (this.queryParams.empName) {
        query.params.empName = this.queryParams.empName
      }
      
      if (this.queryParams.deptName) {
        query.params.deptName = this.queryParams.deptName
      }
      
      if (this.queryParams.year) {
        query.params.year = parseInt(this.queryParams.year)
      }
      
      if (this.queryParams.month) {
        query.params.month = parseInt(this.queryParams.month)
      }
      
      // 使用新的查询对象进行查询
      this.loading = true
      listAttendanceMonthly(query).then(response => {
        this.monthlyList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(error => {
        console.error('Error calling listAttendanceMonthly API:', error)
        this.$modal.msgError('获取月度考勤列表失败，请检查网络或权限')
        this.loading = false
      })
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      // 重置查询参数
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        empId: null,
        empName: null,
        deptName: null,
        year: null,
        month: null,
        params: {}
      }
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          updateAttendanceMonthly(this.form).then(response => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          }).catch(error => {
            console.error('Error calling updateAttendanceMonthly API:', error)
            this.$modal.msgError('修改失败，请检查数据格式或网络连接')
          })
        }
      })
    }
  }
}
</script>
