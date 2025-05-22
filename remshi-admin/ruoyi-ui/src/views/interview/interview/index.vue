<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="面试人" prop="interviewInfoName">
        <el-input
          v-model="queryParams.interviewInfoName"
          placeholder="请输入面试人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="interviewInfoPhone">
        <el-input
          v-model="queryParams.interviewInfoPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="面试时间" prop="interviewInfoTime">
        <el-date-picker clearable
          v-model="queryParams.interviewInfoTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择面试时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="面试官" prop="interviewInfoPeople">
        <el-input
          v-model="queryParams.interviewInfoPeople"
          placeholder="请输入面试官"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="面试结果" prop="interviewInfoStatus">
        <el-select v-model="queryParams.interviewInfoStatus" placeholder="请选择面试结果" clearable>
          <el-option
            v-for="dict in dict.type.interview_info_status"
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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['interview:interview:add']"
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
          v-hasPermi="['interview:interview:edit']"
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
          v-hasPermi="['interview:interview:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['interview:interview:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="interviewList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="id" />
      <el-table-column label="面试人" align="center" prop="interviewInfoName" />
      <el-table-column label="联系电话" align="center" prop="interviewInfoPhone" />
      <el-table-column label="面试时间" align="center" prop="interviewInfoTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.interviewInfoTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="面试官" align="center" prop="interviewInfoPeople" />
      <el-table-column label="面试结果" align="center" prop="interviewInfoStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.interview_info_status" :value="scope.row.interviewInfoStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="creatTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.creatTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['interview:interview:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['interview:interview:remove']"
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

    <!-- 添加或修改面试计划对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="面试人" prop="interviewInfoName">
          <el-input v-model="form.interviewInfoName" placeholder="请输入面试人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="interviewInfoPhone">
          <el-input v-model="form.interviewInfoPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="面试时间" prop="interviewInfoTime">
          <el-date-picker clearable
            v-model="form.interviewInfoTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择面试时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="面试官工号" prop="interviewInfoNumber">
          <el-input v-model="form.interviewInfoNumber" placeholder="请输入面试官工号" />
        </el-form-item>
        <el-form-item label="面试结果" prop="interviewInfoStatus">
          <el-select v-model="form.interviewInfoStatus" placeholder="请选择面试结果">
            <el-option
              v-for="dict in dict.type.interview_info_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="creatTime">
          <el-date-picker clearable
            v-model="form.creatTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
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
import { listInterview, getInterview, delInterview, addInterview, updateInterview } from "@/api/interview/interview";

export default {
  name: "Interview",
  dicts: ['interview_info_status'],
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
      // 面试计划表格数据
      interviewList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        interviewInfoName: null,
        interviewInfoPhone: null,
        interviewInfoTime: null,
        interviewInfoPeople: null,
        interviewInfoStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询面试计划列表 */
    getList() {
      this.loading = true;
      listInterview(this.queryParams).then(response => {
        this.interviewList = response.rows;
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
        id: null,
        interviewInfoName: null,
        interviewInfoPhone: null,
        interviewInfoTime: null,
        interviewInfoPeople: null,
        interviewInfoNumber: null,
        interviewInfoStatus: null,
        creatTime: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加面试计划";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInterview(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改面试计划";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInterview(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInterview(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除面试计划编号为"' + ids + '"的数据项？').then(function() {
        return delInterview(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('interview/interview/export', {
        ...this.queryParams
      }, `interview_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
