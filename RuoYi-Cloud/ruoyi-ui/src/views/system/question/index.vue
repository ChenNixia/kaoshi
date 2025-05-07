<template>
  <div class="app-container">
    <el-row :gutter="20">
      <splitpanes :horizontal="this.$store.getters.device === 'mobile'" class="default-theme">
        <!--用户数据-->
        <pane size="84">
          <el-col>
            <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
              <el-form-item label="题目" prop="questionText">
                <el-input v-model="queryParams.questionText"  clearable style="width: 240px" @keyup.enter.native="handleQuery" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" >新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" >修改</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
              </el-col>
            </el-row>

            <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="题目" align="center" prop="questionText" />
              <el-table-column label="类型" align="center" prop="type" />
              <el-table-column label="选项A" align="center"  prop="optionA"  />
              <el-table-column label="选项B" align="center"  prop="optionB"  />
              <el-table-column label="选项C" align="center"  prop="optionC"  />
              <el-table-column label="选项D" align="center"  prop="optionD"  />
              <el-table-column label="答案" align="center"  prop="answer"  />
              <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
                <template slot-scope="scope" v-if="scope.row.userId !== 1">
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" >修改</el-button>
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" >删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
          </el-col>
        </pane>
      </splitpanes>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type">
                <el-option value="选择题">选择题</el-option>
                <el-option value="填空题">填空题</el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="题目" prop="questionText">
              <el-input v-model="form.questionText" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.type === '选择题'">
          <el-col :span="12">
            <el-form-item label="选项A" prop="optionA">
              <el-input v-model="form.optionA" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选项B" prop="optionB">
              <el-input v-model="form.optionB" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.type === '选择题'">
          <el-col :span="12">
            <el-form-item label="选项C" prop="optionC">
              <el-input v-model="form.optionC" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选项D" prop="optionD">
              <el-input v-model="form.optionD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.type === '选择题'">
          <el-col :span="24">
            <el-form-item label="答案" prop="answer">
              <el-select v-model="form.answer">
                <el-option value="A">A</el-option>
                <el-option value="B">B</el-option>
                <el-option value="C">C</el-option>
                <el-option value="D">D</el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.type === '填空题'">
          <el-col :span="24">
            <el-form-item label="答案" prop="answer">
              <el-input v-model="form.answer" type="textarea"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listQuestion,saveQuestion,delQuestion } from "@/api/system/question";
import { getToken } from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

export default {
  name: "Question",
  components: { Treeselect, Splitpanes, Pane },
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
      form:{

      },
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        questionText: undefined,
      },
      // 表单校验
      rules: {
        questionText: [
          { required: true, message: "题目不能为空", trigger: "blur" },
        ],
        optionA: [
          { required: true, message: "选项A不能为空", trigger: "blur" },
        ],
        optionB: [
          { required: true, message: "选项B不能为空", trigger: "blur" },
        ],
        optionC: [
          { required: true, message: "选项C不能为空", trigger: "blur" },
        ],
        optionD: [
          { required: true, message: "选项D不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "类型不能为空", trigger: "blur" },
        ],
        answer: [
          { required: true, message: "答案不能为空", trigger: "blur" },
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listQuestion(this.queryParams).then(response => {
          this.userList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        questionText: undefined,
        optionA: undefined,
        optionB: undefined,
        optionC: undefined,
        optionD: undefined,
        answer: undefined,
        type: undefined,
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
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.open = true
      this.form = row
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != undefined) {
            saveQuestion(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            saveQuestion(this.form).then(response => {
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
      const userIds = row.id || this.ids;
      debugger
      this.$modal.confirm('是否确认删除数据项？').then(function() {
        return delQuestion(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
  }
};
</script>
