<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工工号" prop="empInfoId">
        <el-input
          v-model="queryParams.empInfoId"
          placeholder="请输入员工工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工姓名" prop="empInfoName">
        <el-input
          v-model="queryParams.empInfoName"
          placeholder="请输入员工姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工性别" prop="empInfoSex">
        <el-select v-model="queryParams.empInfoSex" placeholder="请选择员工性别" clearable>
          <el-option
            v-for="dict in dict.type.sys_user_sex"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <treeselect
          v-model="queryParams.deptId"
          :options="enabledDeptOptions"
          :normalizer="normalizer"
          :show-count="true"
          placeholder="请选择所属部门"
          @change="handleDeptChange"
        />
      </el-form-item>
      <el-form-item label="员工联系方式" prop="empInfoIphone">
        <el-input
          v-model="queryParams.empInfoIphone"
          placeholder="请输入员工联系方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学历" prop="empInfoEducational">
        <el-input
          v-model="queryParams.empInfoEducational"
          placeholder="请输入学历"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入职时间">
        <el-date-picker
          v-model="daterangeEmpInfoEntry"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.emp_info_status"
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
          v-hasPermi="['empInfo:info:add']"
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
          v-hasPermi="['empInfo:info:edit']"
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
          v-hasPermi="['empInfo:info:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['empInfo:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="员工工号" align="center" prop="empInfoId" />
      <el-table-column label="员工姓名" align="center" prop="empInfoName" />
      <el-table-column label="员工性别" align="center" prop="empInfoSex">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.empInfoSex"/>
        </template>
      </el-table-column>
      <el-table-column label="员工年龄" align="center" prop="empInfoAge" />
      <el-table-column label="员工联系方式" align="center" prop="empInfoIphone" />
      <el-table-column label="部门" align="center" key="deptName" prop="deptName" />
      <el-table-column label="学历" align="center" prop="empInfoEducational" />
      <el-table-column label="入职时间" align="center" prop="empInfoEntry" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.empInfoEntry, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.emp_info_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['empInfo:info:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['empInfo:info:remove']"
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

    <!-- 添加或修改员工信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="员工姓名" prop="empInfoName">
          <el-input v-model="form.empInfoName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="员工性别" prop="empInfoSex">
          <el-select v-model="form.empInfoSex" placeholder="请选择员工性别">
            <el-option
              v-for="dict in dict.type.sys_user_sex"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="员工年龄" prop="empInfoAge">
          <el-input v-model="form.empInfoAge" placeholder="请输入员工年龄" />
        </el-form-item>
        <el-form-item label="员工联系方式" prop="empInfoIphone">
          <el-input v-model="form.empInfoIphone" placeholder="请输入员工联系方式" />
        </el-form-item>
        <el-form-item label="归属部门" prop="deptId">
          <treeselect 
            v-model="form.deptId" 
            :options="enabledDeptOptions" 
            :normalizer="normalizer"
            :show-count="true" 
            :disable-branch-nodes="false"
            :loading="deptLoading"
            placeholder="请选择归属部门" />
        </el-form-item>
        <el-form-item label="学历" prop="empInfoEducational">
          <el-input v-model="form.empInfoEducational" placeholder="请输入学历" />
        </el-form-item>
        <el-form-item label="入职时间" prop="empInfoEntry">
          <el-date-picker clearable
            v-model="form.empInfoEntry"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择入职时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.emp_info_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item label="登录密码" prop="passsword">
          <el-input v-model="form.passsword" placeholder="请输入登录密码" />
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
import { listInfo, getInfo, delInfo, addInfo, updateInfo } from "@/api/empInfo/info";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {deptTreeSelect} from "@/api/system/user";

export default {
  name: "Info",
  components: {Treeselect},
  dicts: ['sys_user_sex', 'emp_info_status'],
  data() {
    return {
      deptOptions: [],         // 存储原始部门树数据
      // 过滤掉已禁用部门树选项
      enabledDeptOptions: [],
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
      // 员工信息表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 登录密码时间范围
      daterangeEmpInfoEntry: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        empInfoId: null,
        empInfoName: null,
        empInfoSex: null,
        empInfoIphone: null,
        empInfoEducational: null,
        empInfoEntry: null,
        status: null,
        deptId: undefined,
        deptIds: [] // 存储部门及其子部门的ID数组
      },

      postOptions: [],
      form: {},
      // 表单校验
      rules: {
        empInfoName: [
          { required: true, message: '员工姓名不能为空', trigger: 'blur' }
        ],
        empInfoSex: [
          { required: true, message: '员工性别不能为空', trigger: 'change' }
        ],
        empInfoAge: [
          { required: true, message: '员工年龄不能为空', trigger: 'blur' },
          { pattern: /^[0-9]+$/, message: '员工年龄必须为数字', trigger: 'blur' }
        ],
        empInfoIphone: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        deptId: [
          { required: true, message: '请选择所属部门', trigger: 'change' }
        ],
        empInfoEntry: [
          { required: true, message: '入职时间不能为空', trigger: 'change' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ],
        username: [
          { required: true, message: '登录账号不能为空', trigger: 'blur' }
        ],
        passsword: [
          { required: true, message: '登录密码不能为空', trigger: 'blur' }
        ]
      },
      // 部门树加载状态
      deptLoading: false,
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询部门树结构 */
    getDeptTree() {
      this.deptLoading = true;
      deptTreeSelect()
        .then(response => {
          if (response.data && Array.isArray(response.data)) {
            this.deptOptions = response.data;
            this.enabledDeptOptions = response.data;
            
            // 如果过滤后没有部门，提示用户
            if (this.enabledDeptOptions.length === 0) {
              this.$modal.msgWarning('暂无可用部门，请联系管理员');
            }
          } else {
            this.deptOptions = [];
            this.enabledDeptOptions = [];
            this.$modal.msgWarning('部门数据格式错误，请联系管理员');
          }
          this.deptLoading = false;
        })
        .catch(error => {
          console.error('部门树加载失败:', error);
          this.$modal.msgError('部门数据加载失败，请重试');
          this.deptOptions = [];
          this.enabledDeptOptions = [];
          this.deptLoading = false;
        });
    },
    /** 查询员工信息列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeEmpInfoEntry && '' != this.daterangeEmpInfoEntry) {
        this.queryParams.params['beginEmpInfoEntry'] = this.daterangeEmpInfoEntry[0];
        this.queryParams.params['endEmpInfoEntry'] = this.daterangeEmpInfoEntry[1];
      }
      
      // 构建查询参数
      const queryParams = { ...this.queryParams };
      
      // 如果有部门ID数组且不为空，使用它进行查询
      if (this.queryParams.deptIds && this.queryParams.deptIds.length > 0) {
        queryParams.deptIds = this.queryParams.deptIds;
      }
      
      listInfo(queryParams).then(response => {
        this.infoList = response.rows;
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
        empInfoId: null,
        empInfoName: null,
        empInfoSex: null,
        empInfoAge: null,
        empInfoIphone: null,
        empInfoEducational: null,
        empInfoEntry: null,
        status: null,
        createTime: null,
        username: null,
        deptId: undefined,
        passsword: null
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
      this.daterangeEmpInfoEntry = [];
      this.resetForm('queryForm');
      this.queryParams.deptId = undefined;
      this.queryParams.deptIds = [];
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.empInfoId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加员工信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const empInfoId = row.empInfoId || this.ids
      getInfo(empInfoId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改员工信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.empInfoId != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInfo(this.form).then(response => {
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
      const empInfoIds = row.empInfoId || this.ids;
      this.$modal.confirm('是否确认删除员工信息编号为"' + empInfoIds + '"的数据项？').then(function() {
        return delInfo(empInfoIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('empInfo/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    },
    // Treeselect数据源转换器
    normalizer(node) {
      if (!node) {
        return {};
      }
      // API已经返回了标准的树形结构，只需简单映射即可
      return {
        id: node.id,
        label: node.label,
        children: node.children || []
      };
    },
    /** 部门选择框变更事件处理 */
    handleDeptChange(deptId) {
      // 如果清空了部门选择，则清空deptIds
      if (!deptId) {
        this.queryParams.deptIds = [];
        return;
      }
      
      // 获取所选部门及其所有子部门ID
      this.queryParams.deptIds = this.getChildDeptIds(this.enabledDeptOptions, deptId);
      
      // 确保包含当前选中的部门ID
      if (!this.queryParams.deptIds.includes(deptId)) {
        this.queryParams.deptIds.push(deptId);
      }
    },
    
    /** 递归获取所有子部门ID */
    getChildDeptIds(deptList, parentId, result = []) {
      if (!deptList || !Array.isArray(deptList)) {
        return result;
      }
      
      for (const dept of deptList) {
        if (!dept) continue;
        
        // 如果是目标父部门
        if (dept.id === parentId) {
          // 递归处理子部门
          if (dept.children && dept.children.length) {
            for (const child of dept.children) {
              result.push(child.id);
              // 继续处理子部门的子部门
              if (child.children && child.children.length) {
                this.getChildDeptIds([child], child.id, result);
              }
            }
          }
          return result;
        }
        
        // 如果当前部门有子部门，则递归查找
        if (dept.children && dept.children.length) {
          this.getChildDeptIds(dept.children, parentId, result);
        }
      }
      
      return result;
    },
  }
};
</script>
