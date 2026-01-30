<template>
  <div class="main-content">
    <el-form :model="form" :rules="rules" ref="form" label-width="120px">
      <el-form-item label="通知编号" prop="tongzhibianhao">
        <el-input v-model="form.tongzhibianhao" placeholder="请输入通知编号" disabled></el-input>
      </el-form-item>
      <el-form-item label="预约编号" prop="yuyuebianhao">
        <el-input v-model="form.yuyuebianhao" placeholder="请输入预约编号"></el-input>
      </el-form-item>
      <el-form-item label="医生账号" prop="yishengzhanghao">
        <el-input v-model="form.yishengzhanghao" placeholder="请输入医生账号"></el-input>
      </el-form-item>
      <el-form-item label="用户账号" prop="zhanghao">
        <el-input v-model="form.zhanghao" placeholder="请输入用户账号"></el-input>
      </el-form-item>
      <el-form-item label="通知类型" prop="tongzhitype">
        <el-select v-model="form.tongzhitype" placeholder="请选择通知类型">
          <el-option label="预约成功通知" value="预约成功通知"></el-option>
          <el-option label="就诊前一天提醒" value="就诊前一天提醒"></el-option>
          <el-option label="就诊当天提醒" value="就诊当天提醒"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="接收渠道" prop="jieshouqudao">
        <el-select v-model="form.jieshouqudao" placeholder="请选择接收渠道">
          <el-option label="短信" value="短信"></el-option>
          <el-option label="邮件" value="邮件"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="接收地址" prop="jieshoudizhi">
        <el-input v-model="form.jieshoudizhi" placeholder="请输入接收地址"></el-input>
      </el-form-item>
      <el-form-item label="发送状态" prop="fasongzhuangtai">
        <el-select v-model="form.fasongzhuangtai" placeholder="请选择发送状态">
          <el-option label="待发送" value="待发送"></el-option>
          <el-option label="发送中" value="发送中"></el-option>
          <el-option label="发送成功" value="发送成功"></el-option>
          <el-option label="发送失败" value="发送失败"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="重试次数" prop="chongshicishu">
        <el-input-number v-model="form.chongshicishu" :min="0" :max="10"></el-input-number>
      </el-form-item>
      <el-form-item label="失败原因" prop="shibaiyuanyin">
        <el-input type="textarea" v-model="form.shibaiyuanyin" placeholder="请输入失败原因"></el-input>
      </el-form-item>
      <el-form-item label="处理状态" prop="chulizhuangtai">
        <el-select v-model="form.chulizhuangtai" placeholder="请选择处理状态">
          <el-option label="未处理" value="未处理"></el-option>
          <el-option label="已处理" value="已处理"></el-option>
          <el-option label="已忽略" value="已忽略"></el-option>
          <el-option label="已放弃" value="已放弃"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="处理人" prop="chuliren">
        <el-input v-model="form.chuliren" placeholder="请输入处理人"></el-input>
      </el-form-item>
      <el-form-item label="备注信息" prop="beizhixinxi">
        <el-input type="textarea" v-model="form.beizhixinxi" placeholder="请输入备注信息"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">立即创建</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { create, update, getDetail } from "@/api/tongzhijilu";

export default {
  name: "TongzhijiluForm",
  data() {
    return {
      form: {
        id: undefined,
        tongzhibianhao: "",
        yuyuebianhao: "",
        yishengzhanghao: "",
        zhanghao: "",
        tongzhitype: "",
        jieshouqudao: "",
        jieshoudizhi: "",
        fasongzhuangtai: "待发送",
        chongshicishu: 0,
        shibaiyuanyin: "",
        chulizhuangtai: "未处理",
        chuliren: "",
        beizhixinxi: ""
      },
      rules: {
        yuyuebianhao: [
          { required: true, message: "预约编号不能为空", trigger: "blur" }
        ],
        yishengzhanghao: [
          { required: true, message: "医生账号不能为空", trigger: "blur" }
        ],
        zhanghao: [
          { required: true, message: "用户账号不能为空", trigger: "blur" }
        ],
        tongzhitype: [
          { required: true, message: "通知类型不能为空", trigger: "change" }
        ],
        jieshouqudao: [
          { required: true, message: "接收渠道不能为空", trigger: "change" }
        ],
        jieshoudizhi: [
          { required: true, message: "接收地址不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    // 生成通知编号
    this.form.tongzhibianhao = "TZ" + new Date().getTime();
    
    // 如果有ID参数，则为编辑模式
    const id = this.$route.params.id;
    if (id) {
      this.fetchData(id);
    }
  },
  methods: {
    fetchData(id) {
      getDetail(id).then(response => {
        this.form = response.data;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            update(this.form).then(response => {
              this.$message({
                type: "success",
                message: "修改成功!"
              });
              this.$router.push({ path: "/index/tongzhijiluList" });
            });
          } else {
            create(this.form).then(response => {
              this.$message({
                type: "success",
                message: "创建成功!"
              });
              this.$router.push({ path: "/index/tongzhijiluList" });
            });
          }
        }
      });
    },
    resetForm() {
      this.$refs["form"].resetFields();
      this.form.tongzhibianhao = "TZ" + new Date().getTime();
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