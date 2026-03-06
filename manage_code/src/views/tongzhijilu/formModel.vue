<template>
	<el-dialog :title="formTitle" v-model="formVisible" width="700px">
		<el-form :model="form" label-width="120px">
			<el-form-item label="预约编号">
				<el-input v-model="form.yuyuebianhao" readonly></el-input>
			</el-form-item>
			<el-form-item label="通知类型">
				<el-tag v-if="form.tongzhileixing==1" type="success">预约成功通知</el-tag>
				<el-tag v-else-if="form.tongzhileixing==2" type="warning">就诊前24小时提醒</el-tag>
				<el-tag v-else-if="form.tongzhileixing==3" type="warning">就诊前1小时提醒</el-tag>
				<el-tag v-else-if="form.tongzhileixing==4" type="danger">就诊前15分钟提醒</el-tag>
				<el-tag v-else>未知类型</el-tag>
			</el-form-item>
			<el-form-item label="通知内容">
				<el-input v-model="form.tongzhineirong" type="textarea" :rows="3" readonly></el-input>
			</el-form-item>
			<el-form-item label="接收账号">
				<el-input v-model="form.zhanghao" readonly></el-input>
			</el-form-item>
			<el-form-item label="接收手机">
				<el-input v-model="form.shouji" readonly></el-input>
			</el-form-item>
			<el-form-item label="发送状态">
				<el-tag v-if="form.fasongzhuangtai==0" type="info">待发送</el-tag>
				<el-tag v-else-if="form.fasongzhuangtai==1" type="warning">发送中</el-tag>
				<el-tag v-else-if="form.fasongzhuangtai==2" type="success">发送成功</el-tag>
				<el-tag v-else-if="form.fasongzhuangtai==3" type="danger">发送失败</el-tag>
				<el-tag v-else>未知状态</el-tag>
			</el-form-item>
			<el-form-item label="重试次数">
				<el-input v-model="form.chongshicishu" readonly></el-input>
			</el-form-item>
			<el-form-item label="计划发送时间">
				<el-input v-model="form.jihuafasongshijian" readonly></el-input>
			</el-form-item>
			<el-form-item label="实际发送时间">
				<el-input v-model="form.shijifasongshijian" readonly></el-input>
			</el-form-item>
			<el-form-item label="读取状态">
				<el-tag v-if="form.duquzhuangtai==1" type="success">已读</el-tag>
				<el-tag v-else type="info">未读</el-tag>
			</el-form-item>
			<el-form-item label="读取时间">
				<el-input v-model="form.duqushijian" readonly></el-input>
			</el-form-item>
			<el-form-item label="失败原因" v-if="form.fasongzhuangtai==3">
				<el-input v-model="form.shibaiyuanyin" type="textarea" :rows="2" readonly style="color: #f56c6c;"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="formVisible = false">关闭</el-button>
				<el-button type="primary" v-if="form.fasongzhuangtai==3" @click="retryClick">重试发送</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'

const context = getCurrentInstance()?.appContext.config.globalProperties;
const formVisible = ref(false)
const formTitle = ref('通知详情')
const form = ref({})

const tableName = 'tongzhijilu'

const init = (id, type = 'info') => {
	formVisible.value = true
	if (id) {
		context.$http({
			url: `${tableName}/info/${id}`,
			method: 'get'
		}).then(res => {
			form.value = res.data.data
		})
	}
}

const retryClick = () => {
	context.$http({
		url: `${tableName}/retry`,
		method: 'post',
		data: [form.value.id]
	}).then(res => {
		ElMessage.success('已加入重试队列')
		formVisible.value = false
		emit('formModelChange')
	})
}

const emit = defineEmits(['formModelChange'])

defineExpose({
	init
})
</script>

<style scoped>
.dialog-footer {
	display: flex;
	justify-content: flex-end;
	gap: 10px;
}
</style>
