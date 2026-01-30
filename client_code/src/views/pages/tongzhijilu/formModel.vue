<template>
  <div class="main-content">
    <el-form :model="form" ref="form" label-width="120px">
      <el-form-item label="通知编号">
        <el-input v-model="form.tongzhibianhao" disabled></el-input>
      </el-form-item>
      <el-form-item label="预约编号">
        <el-input v-model="form.yuyuebianhao" disabled></el-input>
      </el-form-item>
      <el-form-item label="医生账号">
        <el-input v-model="form.yishengzhanghao" disabled></el-input>
      </el-form-item>
      <el-form-item label="用户账号">
        <el-input v-model="form.zhanghao" disabled></el-input>
      </el-form-item>
      <el-form-item label="通知类型">
        <el-input v-model="form.tongzhitype" disabled></el-input>
      </el-form-item>
      <el-form-item label="接收渠道">
        <el-input v-model="form.jieshouqudao" disabled></el-input>
      </el-form-item>
      <el-form-item label="接收地址">
        <el-input v-model="form.jieshoudizhi" disabled></el-input>
      </el-form-item>
      <el-form-item label="发送状态">
        <el-tag :type="getStatusType(form.fasongzhuangtai)">{{form.fasongzhuangtai}}</el-tag>
      </el-form-item>
      <el-form-item label="发送时间">
        <el-input v-model="form.fasongshijian" disabled></el-input>
      </el-form-item>
      <el-form-item label="重试次数">
        <el-input v-model="form.chongshicishu" disabled></el-input>
      </el-form-item>
      <el-form-item label="失败原因">
        <el-input type="textarea" v-model="form.shibaiyuanyin" disabled></el-input>
      </el-form-item>
      <el-form-item label="处理状态">
        <el-tag :type="getHandleStatusType(form.chulizhuangtai)">{{form.chulizhuangtai}}</el-tag>
      </el-form-item>
      <el-form-item label="处理时间">
        <el-input v-model="form.chulishijian" disabled></el-input>
      </el-form-item>
      <el-form-item label="处理人">
        <el-input v-model="form.chuliren" disabled></el-input>
      </el-form-item>
      <el-form-item label="备注信息">
        <el-input type="textarea" v-model="form.beizhixinxi" disabled></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getDetail } from "@/api/tongzhijilu";
import { parseTime } from "@/utils/index";

export default {
  name: "TongzhijiluDetail",
  data() {
    return {
      form: {}
    };
  },
  created() {
    const id = this.$route.params.id;
    if (id) {
      this.fetchData(id);
    }
  },
  methods: {
    fetchData(id) {
      getDetail(id).then(response => {
        this.form = response.data;
        // 格式化时间
        if (this.form.fasongshijian) {
          this.form.fasongshijian = parseTime(this.form.fasongshijian);
        }
        if (this.form.chulishijian) {
          this.form.chulishijian = parseTime(this.form.chulishijian);
        }
      });
    },
    goBack() {
      this.$router.push({ path: "/index/tongzhijiluList" });
    },
    getStatusType(status) {
      switch (status) {
        case '发送成功':
          return 'success';
        case '发送失败':
          return 'danger';
        case '发送中':
          return 'warning';
        default:
          return 'info';
      }
    },
    getHandleStatusType(status) {
      switch (status) {
        case '已处理':
          return 'success';
        case '已忽略':
          return 'info';
        case '已放弃':
          return 'warning';
        case '未处理':
          return 'danger';
        default:
          return 'info';
      }
    }
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
</style>