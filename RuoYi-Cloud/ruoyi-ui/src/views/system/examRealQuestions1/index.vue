<template>
  <div class="app-container">
    <el-row :gutter="20" v-if="!flag">
      <splitpanes :horizontal="this.$store.getters.device === 'mobile'" class="default-theme">
        <!--用户数据-->
        <pane size="84">
          <el-col>
            <el-table v-loading="loading" :data="userList" >
              <el-table-column label="名称" align="center" prop="name" />
              <el-table-column label="更新日期" align="center" prop="date" />
              <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope" v-if="scope.row.userId !== 1">
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="handleGr(scope.row)" >开始考试</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
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
              {{o.questionText}}
            </el-form-item>
            <el-form-item label="选项" v-if="o.type === '选择题'">
              <el-radio-group v-model="o.answerA" >
                <el-radio label="A">{{o.optionA}}</el-radio>
                <el-radio label="B">{{o.optionB}}</el-radio>
                <el-radio label="C">{{o.optionC}}</el-radio>
                <el-radio label="D">{{o.optionD}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="'题目'+(i+1)" v-if="o.type === '填空题'">
              {{o.questionText}}
            </el-form-item>
            <el-form-item label="答案" v-if="o.type === '填空题'">
              <el-input  type="textarea"  v-model="o.answerA" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="text-align: center">
          <el-button type="primary" @click="submitForm">提 交</el-button>
          <el-button @click="cancel">取 消</el-button>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
import { listExamRealQuestions,sel } from "@/api/system/examRealQuestions";
import {submitRecord} from '@/api/system/record'
import { getToken } from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { Splitpanes, Pane } from "splitpanes";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import "splitpanes/dist/splitpanes.css";


export default {
  name: "Question",
  components: { Treeselect, Splitpanes, Pane },
  data() {
    return {
      // 遮罩层
      loading: true,
      flag: false,
      stompClient: null,
      cheatingAlert: '',
      isExamStarted: false,
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
      switchCount: 0,
      switchLimit: 3,
      form:{

      },
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      open1: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        questionText: undefined,
      },
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
      listExamRealQuestions(this.queryParams).then(response => {
          this.userList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    submitForm(){
      submitRecord(this.realId,this.list).then(res => {
        this.$alert('你本次考试获得了'+res.data.scope+"分", '考试结果', {
          confirmButtonText: '关闭',
          callback: action => {
            this.flag = false
          }
        });
        this.cleanUpExam();
      })
    },
    // 取消按钮
    cancel() {
      this.flag = false;
      this.cleanUpExam();
    },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
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
    cleanUpExam() {
      if (this.stompClient) {
        this.stompClient.close();
      }
      this.isExamStarted = false;
      this.cheatingAlert = "";
      window.removeEventListener("blur", this.handleScreenSwitch);
    },
    handleScreenSwitch() {
      if (this.isExamStarted) {
        this.switchCount++;
        if (this.switchCount >= this.switchLimit) {
          this.$alert("你已切屏超过3次，考试已被终止！", "警告", {
            confirmButtonText: "确定",
            callback: () => {
              this.cancel();
            }
          });
        } else {
          console.warn(`切屏次数：${this.switchCount}`);
        }
      }
    },
    handleGr(row) {
      this.title = row.name
      this.realId = row.id
      this.switchCount = 0; // 重新计数
      sel(row.id).then(res => {
        this.list = res.data
        this.flag = true
        var token = getToken()
        const socket = new WebSocket('ws://localhost:8080/monitor/ws?authorization=' + token);

        socket.onopen = function () {
          console.log('WebSocket 连接成功');
          socket.send(JSON.stringify({ type: 'connect', token: token }));
        };

        socket.onmessage = function (event) {
          console.log('收到消息:', event.data);
          this.cheatingAlert = event.data;
        };

        socket.onerror = function (error) {
          console.error('WebSocket 发生错误:', error);
        };

        socket.onclose = function () {
          console.log('WebSocket 连接已关闭');
        };
        this.stompClient = socket;
        this.isExamStarted = true;
        window.addEventListener("blur", this.handleScreenSwitch);
      })
    },
  }
};
</script>
