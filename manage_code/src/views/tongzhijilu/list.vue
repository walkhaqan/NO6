<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							预约编号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.yuyuebianhao" placeholder="预约编号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							接收账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="接收账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.fasongzhuangtai" placeholder="请选择发送状态" clearable>
								<el-option label="待发送" :value="0"></el-option>
								<el-option label="发送中" :value="1"></el-option>
								<el-option label="发送成功" :value="2"></el-option>
								<el-option label="发送失败" :value="3"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							读取状态：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.duquzhuangtai" placeholder="请选择读取状态" clearable>
								<el-option label="未读" :value="0"></el-option>
								<el-option label="已读" :value="1"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('tongzhijilu','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="add_btn" type="warning" @click="retryClick()" :disabled="selRows.length?false:true" v-if="btnAuth('tongzhijilu','重试')">
						<i class="iconfont icon-xinzeng1"></i>
						批量重试
					</el-button>
					<el-button class="add_btn" type="success" @click="statisticsClick()" v-if="btnAuth('tongzhijilu','统计')">
						<i class="iconfont icon-xinzeng1"></i>
						发送统计
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('tongzhijilu','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yuyuebianhao"
					label="预约编号">
					<template #default="scope">
						{{scope.row.yuyuebianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhileixing"
					label="通知类型">
					<template #default="scope">
						<el-tag v-if="scope.row.tongzhileixing==1" type="success">预约成功通知</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==2" type="warning">就诊前24小时提醒</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==3" type="warning">就诊前1小时提醒</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==4" type="danger">就诊前15分钟提醒</el-tag>
						<el-tag v-else>未知类型</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhineirong"
					label="通知内容"
					show-overflow-tooltip>
					<template #default="scope">
						{{scope.row.tongzhineirong}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="接收账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="接收手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongzhuangtai"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongzhuangtai==0" type="info">待发送</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==1" type="warning">发送中</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==2" type="success">发送成功</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==3" type="danger">发送失败</el-tag>
						<el-tag v-else>未知状态</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="chongshicishu"
					label="重试次数">
					<template #default="scope">
						{{scope.row.chongshicishu}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jihuafasongshijian"
					label="计划发送时间">
					<template #default="scope">
						{{scope.row.jihuafasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shijifasongshijian"
					label="实际发送时间">
					<template #default="scope">
						{{scope.row.shijifasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="duquzhuangtai"
					label="读取状态">
					<template #default="scope">
						<el-tag v-if="scope.row.duquzhuangtai==1" type="success">已读</el-tag>
						<el-tag v-else type="info">未读</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="duqushijian"
					label="读取时间">
					<template #default="scope">
						{{scope.row.duqushijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shibaiyuanyin"
					label="失败原因"
					show-overflow-tooltip>
					<template #default="scope">
						<span style="color: #f56c6c;">{{scope.row.shibaiyuanyin}}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="250" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('tongzhijilu','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('tongzhijilu','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
						<el-button v-if="scope.row.fasongzhuangtai==3 && btnAuth('tongzhijilu','重试')" type="warning" size="small" @click="singleRetryClick(scope.row.id)">
							重试
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
		<!-- 统计弹窗 -->
		<el-dialog title="发送统计" v-model="statisticsVisible" width="600px">
			<el-row :gutter="20">
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #409EFF;">{{statistics.total}}</div>
							<div style="margin-top: 10px;">总数量</div>
						</div>
					</el-card>
				</el-col>
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #67C23A;">{{statistics.success}}</div>
							<div style="margin-top: 10px;">发送成功</div>
						</div>
					</el-card>
				</el-col>
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #F56C6C;">{{statistics.failed}}</div>
							<div style="margin-top: 10px;">发送失败</div>
						</div>
					</el-card>
				</el-col>
			</el-row>
			<el-row :gutter="20" style="margin-top: 20px;">
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #E6A23C;">{{statistics.pending}}</div>
							<div style="margin-top: 10px;">待发送</div>
						</div>
					</el-card>
				</el-col>
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #909399;">{{statistics.read}}</div>
							<div style="margin-top: 10px;">已读</div>
						</div>
					</el-card>
				</el-col>
				<el-col :span="8">
					<el-card>
						<div style="text-align: center;">
							<div style="font-size: 24px; font-weight: bold; color: #909399;">{{statistics.unread}}</div>
							<div style="margin-top: 10px;">未读</div>
						</div>
					</el-card>
				</el-col>
			</el-row>
		</el-dialog>
	</div>
</template>
<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	//基础信息

	const tableName = 'tongzhijilu'
	const formName = '通知记录'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			//table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.yuyuebianhao&&searchQuery.value.yuyuebianhao!=''){
			params['yuyuebianhao'] = '%' + searchQuery.value.yuyuebianhao + '%'
		}
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.fasongzhuangtai!==undefined&&searchQuery.value.fasongzhuangtai!==''){
			params['fasongzhuangtai'] = searchQuery.value.fasongzhuangtai
		}
		if(searchQuery.value.duquzhuangtai!==undefined&&searchQuery.value.duquzhuangtai!==''){
			params['duquzhuangtai'] = searchQuery.value.duquzhuangtai
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'edit')
			return
		}
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}

	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			// a.click()
			// 下面这个写法兼容火狐
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}

	// 批量重试
	const retryClick = () => {
		if (!selRows.value.length) {
			ElMessage.warning('请选择要重试的记录')
			return
		}
		let ids = []
		for (let x in selRows.value) {
			if (selRows.value[x].fasongzhuangtai == 3) {
				ids.push(selRows.value[x].id)
			}
		}
		if (ids.length == 0) {
			ElMessage.warning('所选记录中没有发送失败的通知')
			return
		}
		context.$http({
			url: `${tableName}/retry`,
			method: 'post',
			data: ids
		}).then(res => {
			ElMessage.success('已加入重试队列')
			getList()
		})
	}

	// 单条重试
	const singleRetryClick = (id) => {
		context.$http({
			url: `${tableName}/retry`,
			method: 'post',
			data: [id]
		}).then(res => {
			ElMessage.success('已加入重试队列')
			getList()
		})
	}

	// 统计
	const statisticsVisible = ref(false)
	const statistics = ref({
		total: 0,
		pending: 0,
		success: 0,
		failed: 0,
		read: 0,
		unread: 0
	})
	const statisticsClick = () => {
		context.$http({
			url: `${tableName}/statistics`,
			method: 'get'
		}).then(res => {
			statistics.value = res.data.data
			statisticsVisible.value = true
		})
	}

	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>

	// 操作盒子
	.list_search_view {
		// 搜索盒子
		.search_form {
			// 子盒子
			.search_view {
				// 搜索label
				.search_label {
				}
				// 搜索item
				.search_box {
					// 输入框
					:deep(.search_inp) {
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				// 搜索按钮
				.search_btn {
				}
				// 搜索按钮-悬浮
				.search_btn:hover {
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			// 其他
			:deep(.el-button--default){
			}
			// 其他-悬浮
			:deep(.el-button--default:hover){
			}
			// 新增
			:deep(.el-button--success){
			}
			// 新增-悬浮
			:deep(.el-button--success:hover){
			}
			// 删除
			:deep(.el-button--danger){
			}
			// 删除-悬浮
			:deep(.el-button--danger:hover){
			}
			// 统计
			:deep(.el-button--warning){
			}
			// 统计-悬浮
			:deep(.el-button--warning:hover){
			}
		}
	}
	// 表格样式
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							// 编辑
							.el-button--primary {
							}
							// 编辑-悬浮
							.el-button--primary:hover {
							}
							// 详情
							.el-button--info {
							}
							// 详情-悬浮
							.el-button--info:hover {
							}
							// 删除
							.el-button--danger {
							}
							// 删除-悬浮
							.el-button--danger:hover {
							}
							// 跨表
							.el-button--success {
							}
							// 跨表-悬浮
							.el-button--success:hover {
							}
							// 操作
							.el-button--warning {
							}
							// 操作-悬浮
							.el-button--warning:hover {
							}
						}
					}
				}
				tr:hover {
					td {
					}
				}
			}
		}
	}
	// 分页器
	.el-pagination {
		// 总页码
		:deep(.el-pagination__total) {
		}
		// 上一页
		:deep(.btn-prev) {
		}
		// 下一页
		:deep(.btn-next) {
		}
		// 上一页禁用
		:deep(.btn-prev:disabled) {
		}
		// 下一页禁用
		:deep(.btn-next:disabled) {
		}
		// 页码
		:deep(.el-pager) {
			// 数字
			.number {
			}
			// 数字悬浮
			.number:hover {
			}
			// 选中
			.number.is-active {
			}
		}
		// sizes
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		// 跳页
		:deep(.el-pagination__jump) {
			// 输入框
			.el-input {
			}
		}
	}
</style>
