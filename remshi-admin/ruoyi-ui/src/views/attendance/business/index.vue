<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人工号" prop="tripInfoId">
        <el-input
          v-model="queryParams.tripInfoId"
          placeholder="请输入申请人工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人" prop="tripInfoName">
        <el-input
          v-model="queryParams.tripInfoName"
          placeholder="请输入申请人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人部门" prop="tripInfoDept">
        <el-input
          v-model="queryParams.tripInfoDept"
          placeholder="请输入申请人部门"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="审批结果" prop="tripInfoResult">
        <el-select v-model="queryParams.tripInfoResult" placeholder="请选择审批结果" clearable>
          <el-option
            v-for="dict in dict.type.business_trip_result"
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
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['trip:business:edit']"
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
          v-hasPermi="['trip:business:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['trip:business:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tripList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="id" />
      <el-table-column label="出差开始日期" align="center" prop="tripInfoDateBegin" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tripInfoDateBegin, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出差结束日期" align="center" prop="tripInfoDateEnd" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tripInfoDateEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请人工号" align="center" prop="tripInfoId" />
      <el-table-column label="申请人" align="center" prop="tripInfoName" />
      <el-table-column label="申请人部门" align="center" prop="tripInfoDept" />
      <el-table-column label="出差目的地" align="center" prop="tripInfoDestination" />
      <el-table-column label="出差原因" align="center" prop="tripInfoReason" :show-overflow-tooltip="true" />
      <el-table-column label="审批人" align="center" prop="tripInfoPeople" />
      <el-table-column label="审批结果" align="center" prop="tripInfoResult">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.tripInfoResult === 4" type="info">撤销申请</el-tag>
          <dict-tag v-else :options="dict.type.business_trip_result" :value="scope.row.tripInfoResult"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['trip:business:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['trip:business:remove']"
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

    <!-- 添加或修改出差审批对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="出差开始日期" prop="tripInfoDateBegin">
          <el-date-picker clearable
            v-model="form.tripInfoDateBegin"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择出差开始日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="出差结束日期" prop="tripInfoDateEnd">
          <el-date-picker clearable
            v-model="form.tripInfoDateEnd"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择出差结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="申请人工号" prop="tripInfoId">
          <el-input v-model="form.tripInfoId" placeholder="请输入申请人工号" />
        </el-form-item>
        <el-form-item label="申请人" prop="tripInfoName">
          <el-input v-model="form.tripInfoName" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item label="申请人部门" prop="tripInfoDept">
          <el-input v-model="form.tripInfoDept" placeholder="请输入申请人部门" />
        </el-form-item>
        <el-form-item label="出差目的地" prop="tripInfoDestination">
          <el-input v-model="form.tripInfoDestination" placeholder="请输入出差目的地" />
        </el-form-item>
        <el-form-item label="出差原因" prop="tripInfoReason">
          <el-input v-model="form.tripInfoReason" type="textarea" placeholder="请输入出差原因" />
        </el-form-item>
        <el-form-item label="审批人" prop="tripInfoPeople">
          <el-input v-model="form.tripInfoPeople" placeholder="请输入审批人" />
        </el-form-item>
        <el-form-item label="审批结果" prop="tripInfoResult">
          <el-select v-model="form.tripInfoResult" placeholder="请选择审批结果">
            <el-option
              v-for="dict in dict.type.business_trip_result"
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
import { listBusinessTrip, getBusinessTrip, delBusinessTrip, updateBusinessTrip } from "@/api/attendance/business/index";

export default {
  name: "BusinessTrip",
  dicts: ['business_trip_result'],
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
      // 出差申请表格数据
      tripList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tripInfoId: null,
        tripInfoName: null,
        tripInfoDept: null,
        tripInfoPeople: null,
        tripInfoResult: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        tripInfoDateBegin: [
          { required: true, message: "出差开始日期不能为空", trigger: "blur" }
        ],
        tripInfoDateEnd: [
          { required: true, message: "出差结束日期不能为空", trigger: "blur" }
        ],
        tripInfoId: [
          { required: true, message: "申请人工号不能为空", trigger: "blur" }
        ],
        tripInfoName: [
          { required: true, message: "申请人不能为空", trigger: "blur" }
        ],
        tripInfoDept: [
          { required: true, message: "申请人部门不能为空", trigger: "blur" }
        ],
        tripInfoDestination: [
          { required: true, message: "出差目的地不能为空", trigger: "blur" }
        ],
        tripInfoReason: [
          { required: true, message: "出差原因不能为空", trigger: "blur" }
        ],
        tripInfoPeople: [
          { required: true, message: "审批人不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询出差审批列表 */
    getList() {
      this.loading = true;
      listBusinessTrip(this.queryParams).then(response => {
        this.tripList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        console.error("Error calling listBusinessTrip API:", error);
        this.$modal.msgError("获取出差审批列表失败，请检查网络或权限");
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
        tripInfoDateBegin: null,
        tripInfoDateEnd: null,
        tripInfoId: null,
        tripInfoName: null,
        tripInfoDept: null,
        tripInfoDestination: null,
        tripInfoReason: null,
        tripInfoPeople: null,
        tripInfoResult: 0,  // 默认为一级审批中状态
        createTime: null,
        remark: null
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
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      console.log("Getting business trip detail with ID:", id);
      getBusinessTrip(id).then(response => {
        console.log("getBusinessTrip API response:", response);
        this.form = response.data;
        this.open = true;
        this.title = "修改出差审批";
      }).catch(error => {
        console.error("Error calling getBusinessTrip API:", error);
        this.$modal.msgError("获取出差详情失败，请检查网络或权限");
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            console.log("Updating business trip with data:", this.form);
            updateBusinessTrip(this.form).then(response => {
              console.log("updateBusinessTrip API response:", response);
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(error => {
              console.error("Error calling updateBusinessTrip API:", error);
              this.$modal.msgError("修改失败，请检查数据格式或网络连接");
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除出差审批编号为"' + ids + '"的数据项？').then(function() {
        console.log("Deleting business trip with ID:", ids);
        return delBusinessTrip(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch((error) => {
        console.error("Error calling delBusinessTrip API:", error);
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('trip/business/export', {
        ...this.queryParams
      }, `business_trip_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
