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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['salary:baseInfo:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['salary:baseInfo:edit']"
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['salary:baseInfo:remove']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['salary:baseInfo:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="baseInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="员工工号" align="center" prop="empId" />
      <el-table-column label="基本工资" align="center" prop="baseSalary">
        <template slot-scope="scope">
          {{ scope.row.baseSalary }} 元
        </template>
      </el-table-column>
      <el-table-column label="标准出勤天数" align="center" prop="standardAttendanceDays" />
      <el-table-column label="小时工资" align="center" prop="hourSalary">
        <template slot-scope="scope">
          {{ scope.row.hourSalary }} 元
        </template>
      </el-table-column>
      <el-table-column label="日工资" align="center" prop="daySalary">
        <template slot-scope="scope">
          {{ scope.row.daySalary }} 元
        </template>
      </el-table-column>
      <el-table-column label="绩效基数" align="center" prop="performanceBase">
        <template slot-scope="scope">
          {{ scope.row.performanceBase }} 元
        </template>
      </el-table-column>
      <el-table-column label="迟到/早退扣款" align="center" prop="lateDeduction">
        <template slot-scope="scope">
          {{ scope.row.lateDeduction }} 元/次
        </template>
      </el-table-column>
      <el-table-column label="旷工扣款比例" align="center" prop="absenceDeductionRatio">
        <template slot-scope="scope">
          {{ scope.row.absenceDeductionRatio }} 倍
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['salary:baseInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['salary:baseInfo:remove']"
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

    <!-- 添加或修改基本工资信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="员工工号" prop="empId">
          <el-input v-model="form.empId" placeholder="请输入员工工号" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="基本工资" prop="baseSalary">
              <el-input-number v-model="form.baseSalary" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%;" @change="calculateDerivedValues" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准出勤天数" prop="standardAttendanceDays">
              <el-input-number v-model="form.standardAttendanceDays" :precision="0" :step="1" :min="1" :max="31" controls-position="right" style="width: 100%;" @change="calculateDerivedValues" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="日工资" prop="daySalary">
              <el-input-number v-model="form.daySalary" :precision="2" :step="10" :min="0" controls-position="right" disabled style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="小时工资" prop="hourSalary">
              <el-input-number v-model="form.hourSalary" :precision="2" :step="10" :min="0" controls-position="right" disabled style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="绩效基数" prop="performanceBase">
              <el-input-number v-model="form.performanceBase" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="迟到/早退扣款" prop="lateDeduction">
              <el-input-number v-model="form.lateDeduction" :precision="2" :step="10" :min="0" controls-position="right" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="旷工扣款比例" prop="absenceDeductionRatio">
              <el-input-number v-model="form.absenceDeductionRatio" :precision="2" :step="0.1" :min="1" controls-position="right" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="加班工资比例" prop="overtimeRatio">
              <el-input-number v-model="form.overtimeRatio" :precision="2" :step="0.1" :min="1" controls-position="right" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="出差补贴比例" prop="businessTripRatio">
              <el-input-number v-model="form.businessTripRatio" :precision="2" :step="0.1" :min="1" controls-position="right" style="width: 100%;" />
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
  </div>
</template>

<script>
import { listSalaryBaseInfo, getSalaryBaseInfo, delSalaryBaseInfo, addSalaryBaseInfo, updateSalaryBaseInfo } from "@/api/salary/baseInfo";
import request from "@/utils/request";

export default {
  name: "SalaryBaseInfo",
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
      // 基本工资信息表格数据
      baseInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 当前操作类型（add:新增，edit:修改）
      operationType: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        empId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        empId: [
          { required: true, message: "员工工号不能为空", trigger: "blur" }
        ],
        baseSalary: [
          { required: true, message: "基本工资不能为空", trigger: "blur" }
        ],
        standardAttendanceDays: [
          { required: true, message: "标准出勤天数不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询基本工资信息列表 */
    getList() {
      this.loading = true;
      listSalaryBaseInfo(this.queryParams).then(response => {
        this.baseInfoList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        console.error("获取基本工资信息失败", error);
        this.$modal.msgError("获取基本工资信息失败");
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
        id: null,
        empId: "",
        baseSalary: 0,
        standardAttendanceDays: 22,
        hourSalary: 0,
        daySalary: 0,
        performanceBase: 0,
        lateDeduction: 50,
        absenceDeductionRatio: 1.5,
        overtimeRatio: 1.5,
        businessTripRatio: 1.5,
        remark: ""
      };
      this.resetForm("form");
      this.calculateDerivedValues();
    },
    /** 计算派生值（日工资和小时工资） */
    calculateDerivedValues() {
      if (this.form.baseSalary && this.form.standardAttendanceDays) {
        // 计算日工资 = 基本工资 / 标准出勤天数
        const daySalary = this.form.baseSalary / this.form.standardAttendanceDays;
        this.form.daySalary = parseFloat(daySalary.toFixed(2));
        
        // 计算小时工资 = 日工资 / 8（假设每天工作8小时）
        const hourSalary = daySalary / 8;
        this.form.hourSalary = parseFloat(hourSalary.toFixed(2));
      }
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
      this.ids = selection.map(item => item.empId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加基本工资信息";
      this.operationType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.empId || this.ids;
      getSalaryBaseInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改基本工资信息";
        this.operationType = "edit";
      }).catch(error => {
        console.error("获取基本工资信息详情失败", error);
        this.$modal.msgError("获取基本工资信息详情失败");
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 提交前重新计算派生值
          this.calculateDerivedValues();
          
          if (this.operationType === "edit") {
            updateSalaryBaseInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("修改基本工资信息失败", error);
            });
          } else {
            addSalaryBaseInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("新增基本工资信息失败", error);
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.empId || this.ids;
      this.$modal.confirm('是否确认删除基本工资信息编号为"' + ids + '"的数据项？').then(() => {
        return delSalaryBaseInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有基本工资信息数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.download('/salary/base/export', {
          ...this.queryParams
        }, `基本工资信息_${new Date().getTime()}.xlsx`);
      });
    }
  }
};
</script>

<style scoped>
.el-input-number {
  width: 100%;
}
</style> 