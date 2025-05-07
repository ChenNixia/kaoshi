<template>
  <div class="app-container">
    <el-row :gutter="20" v-if="!flag">
      <splitpanes :horizontal="this.$store.getters.device === 'mobile'" class="default-theme">
        <!--用户数据-->
        <pane size="84">
          <el-col>
            <el-table v-loading="loading" :data="userList" >
              <el-table-column label="考卷名称" align="center" prop="name" />
              <el-table-column label="分数" align="center" prop="scope" />
              <el-table-column label="考试日期" align="center" prop="date" >        <template slot-scope="scope">
                <span>{{ parseTime(scope.row.date) }}</span>
              </template></el-table-column>
              <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope" v-if="scope.row.userId !== 1">
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="handleGr(scope.row)" >详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </pane>
      </splitpanes>
    </el-row>
    <div v-if="flag">
      <h1 style="text-align: center">{{title}}</h1>
      <el-form ref="form" label-width="80px">
        <el-row v-for="(o,i) in list" :key="i">
          <el-col :span="24">
            <el-form-item :label="'题目'+(i+1)" v-if="o.type === '选择题'">
              {{o.question_text}}
            </el-form-item>
            <el-form-item label="得分" v-if="o.type === '选择题'">
              {{o.scope}}
            </el-form-item>
            <el-form-item label="选项" v-if="o.type === '选择题'">
              <el-radio-group v-model="o.answerA" disabled>
                <el-radio label="A">{{o.option_a}}</el-radio>
                <el-radio label="B">{{o.option_b}}</el-radio>
                <el-radio label="C">{{o.option_c}}</el-radio>
                <el-radio label="D">{{o.option_d}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="标准答案" v-if="o.type === '选择题'">
              {{o.answer}}
            </el-form-item>
            <el-form-item :label="'题目'+(i+1)" v-if="o.type === '填空题'">
              {{o.question_text}}
            </el-form-item>
            <el-form-item label="得分" v-if="o.type === '填空题'">
              {{o.scope}}
            </el-form-item>
            <el-form-item label="我的答案" v-if="o.type === '填空题'">
              <el-input  type="textarea"  v-model="o.answerA" readonly/>
            </el-form-item>
            <el-form-item label="标准答案" v-if="o.type === '填空题'">
              <el-input  type="textarea"  v-model="o.answer" readonly/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="text-align: center">
          <el-button @click="cancel">返 回</el-button>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
import {selRecord,findAll} from '@/api/system/record'
import { getToken } from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";


export default {
  name: "Record",
  components: { Treeselect, Splitpanes, Pane },
  data() {
    return {
      // 遮罩层
      loading: true,
      flag: false,
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
      list:[],
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      realId: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      selRecord().then(response => {
          this.userList = response.data;
          this.loading = false;
        }
      );
    },
    cancel(){
      this.flag = false
    },
    handleGr(row){
      this.flag = true
      findAll(row.id).then(res => {
        this.list = res.data
      })
    }
  }
};
</script>
