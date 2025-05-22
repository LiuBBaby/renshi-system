<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人工号" prop="examineInfoId">
        <el-input
          v-model="queryParams.examineInfoId"
          placeholder="请输入申请人工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人" prop="examineInfoName">
        <el-input
          v-model="queryParams.examineInfoName"
          placeholder="请输入申请人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人部门" prop="examineInfoDept">
        <el-input
          v-model="queryParams.examineInfoDept"
          placeholder="请输入申请人部门"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审批人" prop="examineInfoPeople">
        <el-input
          v-model="queryParams.examineInfoPeople"
          placeholder="请输入审批人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审批结果 " prop="examineInfoResult">
        <el-select v-model="queryParams.examineInfoResult" placeholder="请选择审批结果 " clearable>
          <el-option
            v-for="dict in dict.type.examine_inforesult"
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
          v-hasPermi="['examine:examine:add']"
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
          v-hasPermi="['examine:examine:edit']"
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
          v-hasPermi="['examine:examine:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['examine:examine:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="examineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="id" />
      <el-table-column label="请假开始日期" align="center" prop="examineInfoDateBegin" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.examineInfoDateBegin, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假结束日期" align="center" prop="examineInfoDateEnd" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.examineInfoDateEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请人工号" align="center" prop="examineInfoId" width="100" />
      <el-table-column label="申请人" align="center" prop="examineInfoName" width="100" />
      <el-table-column label="申请人部门" align="center" prop="examineInfoDept" width="120" />
      <el-table-column label="请假原因" align="center" prop="examineInfoReason" />
      <el-table-column label="审批人" align="center" prop="examineInfoPeople" width="100" />
      <el-table-column label="审批结果 " align="center" prop="examineInfoResult">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.examineInfoResult === 4" type="info">撤销申请</el-tag>
          <dict-tag v-else :options="dict.type.examine_inforesult" :value="scope.row.examineInfoResult"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['examine:examine:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['examine:examine:remove']"
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

    <!-- 添加或修改请假审批对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="请假开始日期" prop="examineInfoDateBegin">
          <el-date-picker clearable
            v-model="form.examineInfoDateBegin"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择请假开始日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="请假结束日期" prop="examineInfoDateEnd">
          <el-date-picker clearable
            v-model="form.examineInfoDateEnd"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择请假结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="申请人工号" prop="examineInfoId">
          <el-input v-model="form.examineInfoId" placeholder="请输入申请人工号" />
        </el-form-item>
        <el-form-item label="申请人" prop="examineInfoName">
          <el-input v-model="form.examineInfoName" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item label="申请人部门" prop="examineInfoDept">
          <el-input v-model="form.examineInfoDept" placeholder="请输入申请人部门" />
        </el-form-item>
        <el-form-item label="请假原因" prop="examineInfoReason">
          <el-input v-model="form.examineInfoReason" placeholder="请输入请假原因" />
        </el-form-item>
        <el-form-item label="审批人" prop="examineInfoPeople">
          <el-input v-model="form.examineInfoPeople" placeholder="请输入审批人" />
        </el-form-item>
        <el-form-item label="审批结果 " prop="examineInfoResult">
          <el-select v-model="form.examineInfoResult" placeholder="请选择审批结果 ">
            <el-option
              v-for="dict in dict.type.examine_inforesult"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
            <el-option
              :key="4"
              label="撤销申请"
              :value="4"
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
import { listExamine, getExamine, delExamine, addExamine, updateExamine } from "@/api/examine/examine";

export default {
  name: "Examine",
  dicts: ['examine_inforesult'],
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
      // 请假审批表格数据
      examineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        examineInfoId: null,
        examineInfoName: null,
        examineInfoDept: null,
        examineInfoPeople: null,
        examineInfoResult: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        examineInfoDateBegin: [
          { required: true, message: "请假开始日期不能为空", trigger: "blur" }
        ],
        examineInfoDateEnd: [
          { required: true, message: "请假结束日期不能为空", trigger: "blur" }
        ],
        examineInfoId: [
          { required: true, message: "申请人工号不能为空", trigger: "blur" }
        ],
        examineInfoName: [
          { required: true, message: "申请人不能为空", trigger: "blur" }
        ],
        examineInfoDept: [
          { required: true, message: "申请人部门不能为空", trigger: "blur" }
        ],
        examineInfoReason: [
          { required: true, message: "请假原因不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    console.log("Examine component created, calling getList()");
    this.getList();
  },
  methods: {
    /** 查询请假审批列表 */
    getList() {
      this.loading = true;
      console.log("Calling listExamine API with params:", this.queryParams);
      listExamine(this.queryParams).then(response => {
        console.log("listExamine API response:", response);
        this.examineList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        console.error("Error calling listExamine API:", error);
        this.$modal.msgError("获取请假审批列表失败，请检查网络或权限");
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
        examineInfoDateBegin: null,
        examineInfoDateEnd: null,
        examineInfoId: null,
        examineInfoName: null,
        examineInfoDept: null,
        examineInfoReason: null,
        examineInfoPeople: null,
        examineInfoResult: 0,  // 默认为审批中状态
        createTime: null
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
      this.title = "添加请假审批";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      console.log("Getting examine detail with ID:", id);
      getExamine(id).then(response => {
        console.log("getExamine API response:", response);
        this.form = response.data;
        this.open = true;
        this.title = "修改请假审批";
      }).catch(error => {
        console.error("Error calling getExamine API:", error);
        this.$modal.msgError("获取请假详情失败，请检查网络或权限");
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            console.log("Updating examine with data:", this.form);
            updateExamine(this.form).then(response => {
              console.log("updateExamine API response:", response);
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("Error calling updateExamine API:", error);
              this.$modal.msgError("修改失败，请检查数据格式或网络连接");
            });
          } else {
            console.log("Adding new examine with data:", this.form);
            addExamine(this.form).then(response => {
              console.log("addExamine API response:", response);
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("Error calling addExamine API:", error);
              this.$modal.msgError("新增失败，请检查数据格式或网络连接");
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除请假审批编号为"' + ids + '"的数据项？').then(function() {
        console.log("Deleting examine with ID:", ids);
        return delExamine(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch((error) => {
        console.error("Error calling delExamine API:", error);
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('examine/examine/export', {
        ...this.queryParams
      }, `examine_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
