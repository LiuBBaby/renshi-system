<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="招聘岗位" prop="recruitmentInfoPost">
        <el-input
          v-model="queryParams.recruitmentInfoPost"
          placeholder="请输入招聘岗位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学历要求" prop="recruitmentInfoDegree">
        <el-select v-model="queryParams.recruitmentInfoDegree" placeholder="请选择学历要求" clearable>
          <el-option
            v-for="dict in dict.type.sys_educational"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人工号" prop="recruitmentInfoId">
        <el-input
          v-model="queryParams.recruitmentInfoId"
          placeholder="请输入负责人工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="招聘负责人" prop="recruitmentInfoResponsible">
        <el-input
          v-model="queryParams.recruitmentInfoResponsible"
          placeholder="请输入招聘负责人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="招聘状态" prop="recruitmentInfoStatus">
        <el-select v-model="queryParams.recruitmentInfoStatus" placeholder="请选择招聘状态" clearable>
          <el-option
            v-for="dict in dict.type.recruitment_info_status"
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
          v-hasPermi="['recruitment:recruitment:add']"
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
          v-hasPermi="['recruitment:recruitment:edit']"
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
          v-hasPermi="['recruitment:recruitment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['recruitment:recruitment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recruitmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="id" />
      <el-table-column label="需求人数" align="center" prop="recruitmentInfoNumber" />
      <el-table-column label="招聘岗位" align="center" prop="recruitmentInfoPost" />
      <el-table-column label="学历要求" align="center" prop="recruitmentInfoDegree">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_educational" :value="scope.row.recruitmentInfoDegree"/>
        </template>
      </el-table-column>
      <el-table-column label="工作年限" align="center" prop="recruitmentInfoDuration" />
      <el-table-column label="招聘需求" align="center" prop="recruitmentInfoDemand" />
      <el-table-column label="负责人工号" align="center" prop="recruitmentInfoId" />
      <el-table-column label="招聘负责人" align="center" prop="recruitmentInfoResponsible" />
      <el-table-column label="招聘状态" align="center" prop="recruitmentInfoStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.recruitment_info_status" :value="scope.row.recruitmentInfoStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['recruitment:recruitment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['recruitment:recruitment:remove']"
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

    <!-- 添加或修改招聘需求对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="需求人数" prop="recruitmentInfoNumber">
          <el-input v-model="form.recruitmentInfoNumber" placeholder="请输入需求人数" />
        </el-form-item>
        <el-form-item label="招聘岗位" prop="recruitmentInfoPost">
          <el-input v-model="form.recruitmentInfoPost" placeholder="请输入招聘岗位" />
        </el-form-item>
        <el-form-item label="学历要求" prop="recruitmentInfoDegree">
          <el-select v-model="form.recruitmentInfoDegree" placeholder="请选择学历要求">
            <el-option
              v-for="dict in dict.type.sys_educational"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作年限" prop="recruitmentInfoDuration">
          <el-input v-model="form.recruitmentInfoDuration" placeholder="请输入工作年限" />
        </el-form-item>
        <el-form-item label="招聘需求" prop="recruitmentInfoDemand">
          <el-input v-model="form.recruitmentInfoDemand" placeholder="请输入招聘需求" />
        </el-form-item>
        <el-form-item label="负责人工号" prop="recruitmentInfoId">
          <el-input v-model="form.recruitmentInfoId" placeholder="请输入负责人工号" />
        </el-form-item>
        <el-form-item label="招聘状态" prop="recruitmentInfoStatus">
          <el-select v-model="form.recruitmentInfoStatus" placeholder="请选择招聘状态">
            <el-option
              v-for="dict in dict.type.recruitment_info_status"
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
import { listRecruitment, getRecruitment, delRecruitment, addRecruitment, updateRecruitment } from "@/api/recruitment/recruitment";

export default {
  name: "Recruitment",
  dicts: ['recruitment_info_status', 'sys_educational'],
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
      // 招聘需求表格数据
      recruitmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recruitmentInfoPost: null,
        recruitmentInfoDegree: null,
        recruitmentInfoId: null,
        recruitmentInfoResponsible: null,
        recruitmentInfoStatus: null,
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
    /** 查询招聘需求列表 */
    getList() {
      this.loading = true;
      listRecruitment(this.queryParams).then(response => {
        this.recruitmentList = response.rows;
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
        recruitmentInfoNumber: null,
        recruitmentInfoPost: null,
        recruitmentInfoDegree: null,
        recruitmentInfoDuration: null,
        recruitmentInfoDemand: null,
        recruitmentInfoId: null,
        recruitmentInfoResponsible: null,
        recruitmentInfoStatus: null,
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
      this.title = "添加招聘需求";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRecruitment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改招聘需求";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRecruitment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRecruitment(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除招聘需求编号为"' + ids + '"的数据项？').then(function() {
        return delRecruitment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('recruitment/recruitment/export', {
        ...this.queryParams
      }, `recruitment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
