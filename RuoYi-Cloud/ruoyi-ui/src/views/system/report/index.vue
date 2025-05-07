<template>
  <div class="chart-container">
    <div ref="barChart" class="chart"></div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import {findUser} from "../../../api/system/record";

export default {
  name: "Report",
  data() {
    return {
      chartInstance: null,
      keys:[],
      values:[]
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList(){
      findUser().then(res => {
        this.keys = res.data.keys;
        this.values = res.data.values;

        var myChart = echarts.init(this.$refs.barChart);
        myChart.setOption({
          title: {
            text: '考生分数统计'
          },
          tooltip: {},
          xAxis: {
            data: this.keys
          },
          yAxis: {},
          series: [
            {
              name: '总分数',
              type: 'bar',
              data: this.values
            }
          ]
        });
      })
    },
    resizeChart() {
      if (this.chartInstance) {
        this.chartInstance.resize();
      }
    },
    destroyChart() {
      if (this.chartInstance) {
        this.chartInstance.dispose();
        this.chartInstance = null;
      }
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeChart);
    this.destroyChart();
  },
};
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 400px;
}
.chart {
  width: 100%;
  height: 100%;
}
</style>
