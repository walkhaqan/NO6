<template>
  <div class="main-content">
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="handleBatchRetry" :disabled="multipleSelection.length === 0">批量重试</el-button>
      <el-button type="success" size="small" icon="el-icon-check" @click="handleBatchHandle" :disabled="multipleSelection.length === 0">批量处理</el-button>
      <el-button type="info" size="small" icon="el-icon-refresh" @click="getList">刷新</el-button>
    </div>
    
    <el-table :data="list" v-loading="listLoading" @selection-change="handleSelectionChange" border style="width: 100%">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="tongzhibianhao" label="通知编号" width="180"></el-table-column>
      <el-table-column prop="yuyuebianhao" label="预约编号" width="180"></el-table-column>
      <el-table-column prop="yishengzhanghao" label="医生账号" width="120"></el-table-column>
      <el-table-column prop="zhanghao" label="用户账号" width="120"></el-table-column>
      <el-table-column prop="tongzhitype" label="通知类型" width="120"></el-table-column>
      <el-table-column prop="jieshouqudao" label="接收渠道" width="100"></el-table-column>
      <el-table-column prop="jieshoudizhi" label="接收地址" width="150"></el-table-column>
      <el-table-column prop="fasongzhuangtai" label="发送状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.fasongzhuangtai)">{{scope.row.fasongzhuangtai}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fasongshijian" label="发送时间" width="160">
        <template slot-scope="scope">
          {{parseTime(scope.row.fasongshijian)}}
        </template>
      </el-table-column>
      <el-table-column prop="chongshicishu" label="重试次数" width="100"></el-table-column>
      <el-table-column prop="chulizhuangtai" label="处理状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getHandleStatusType(scope.row.chulizhuangtai)">{{scope.row.chulizhuangtai}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleRetry(scope.row)" v-if="scope.row.fasongzhuangtai === '发送失败'">重试</el-button>
          <el-button type="success" size="mini" @click="handleHandle(scope.row)" v-if="scope.row.fasongzhuangtai === '发送失败'">处理</el-button>
          <el-button type="info" size="mini" @click="handleDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="listQuery.page"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="listQuery.limit"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
    
    <!-- 处理失败通知对话框 -->
    <el-dialog title="处理失败通知" :visible.sync="dialogVisible" width="500px">
      <el-form :model="handleForm" ref="handleForm" label-width="100px">
        <el-form-item label="处理状态" prop="chulizhuangtai">
          <el-select v-model="handleForm.chulizhuangtai" placeholder="请选择处理状态">
            <el-option label="已处理" value="已处理"></el-option>
            <el-option label="已忽略" value="已忽略"></el-option>
            <el-option label="已放弃" value="已放弃"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息" prop="beizhixinxi">
          <el-input type="textarea" v-model="handleForm.beizhixinxi" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog title="通知详情" :visible.sync="detailDialogVisible" width="600px">
      <el-form :model="detailForm" label-width="100px">
        <el-form-item label="通知编号">{{detailForm.tongzhibianhao}}</el-form-item>
        <el-form-item label="预约编号">{{detailForm.yuyuebianhao}}</el-form-item>
        <el-form-item label="医生账号">{{detailForm.yishengzhanghao}}</el-form-item>
        <el-form-item label="用户账号">{{detailForm.zhanghao}}</el-form-item>
        <el-form-item label="通知类型">{{detailForm.tongzhitype}}</el-form-item>
        <el-form-item label="接收渠道">{{detailForm.jieshouqudao}}</el-form-item>
        <el-form-item label="接收地址">{{detailForm.jieshoudizhi}}</el-form-item>
        <el-form-item label="发送状态">
          <el-tag :type="getStatusType(detailForm.fasongzhuangtai)">{{detailForm.fasongzhuangtai}}</el-tag>
        </el-form-item>
        <el-form-item label="发送时间">{{parseTime(detailForm.fasongshijian)}}</el-form-item>
        <el-form-item label="重试次数">{{detailForm.chongshicishu}}</el-form-item>
        <el-form-item label="失败原因">{{detailForm.shibaiyuanyin || '无'}}</el-form-item>
        <el-form-item label="处理状态">
          <el-tag :type="getHandleStatusType(detailForm.chulizhuangtai)">{{detailForm.chulizhuangtai}}</el-tag>
        </el-form-item>
        <el-form-item label="处理时间">{{parseTime(detailForm.chulishijian)}}</el-form-item>
        <el-form-item label="处理人">{{detailForm.chuliren || '无'}}</el-form-item>
        <el-form-item label="备注信息">{{detailForm.beizhixinxi || '无'}}</el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { list, retryNotification, handleFailedNotification, batchRetry, batchHandle } from "@/api/tongzhijilu";
import { parseTime } from "@/utils/index";

export default {
  name: "TongzhijiluList",
  data() {
    return {
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20
      },
      multipleSelection: [],
      dialogVisible: false,
      detailDialogVisible: false,
      handleForm: {
        id: null,
        chulizhuangtai: "已处理",
        beizhixinxi: ""
      },
      detailForm: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      list(this.listQuery).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.listLoading = false;
      });
    },
    handleSizeChange(val) {
      this.listQuery.limit = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.page = val;
      this.getList();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleRetry(row) {
      this.$confirm('确认重试发送该通知吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        retryNotification(row.id).then(response => {
          if (response.code === 0) {
            this.$message({
              type: 'success',
              message: '重试成功!'
            });
            this.getList();
          } else {
            this.$message({
              type: 'error',
              message: response.msg || '重试失败'
            });
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消重试'
        });          
      });
    },
    handleHandle(row) {
      this.handleForm.id = row.id;
      this.handleForm.chulizhuangtai = "已处理";
      this.handleForm.beizhixinxi = "";
      this.dialogVisible = true;
    },
    submitHandle() {
      handleFailedNotification(
        this.handleForm.id,
        this.handleForm.chulizhuangtai,
        this.handleForm.beizhixinxi
      ).then(response => {
        if (response.code === 0) {
          this.$message({
            type: 'success',
            message: '处理成功!'
          });
          this.dialogVisible = false;
          this.getList();
        } else {
          this.$message({
            type: 'error',
            message: response.msg || '处理失败'
          });
        }
      });
    },
    handleDetail(row) {
      this.detailForm = Object.assign({}, row);
      this.detailDialogVisible = true;
    },
    handleBatchRetry() {
      const ids = this.multipleSelection.map(item => item.id);
      this.$confirm('确认批量重试选中的通知吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        batchRetry(ids).then(response => {
          if (response.code === 0) {
            this.$message({
              type: 'success',
              message: response.msg || '批量重试成功!'
            });
            this.getList();
          } else {
            this.$message({
              type: 'error',
              message: response.msg || '批量重试失败'
            });
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消批量重试'
        });          
      });
    },
    handleBatchHandle() {
      this.$prompt('请输入处理状态', '批量处理', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'select',
        inputOptions: [
          { value: '已处理', label: '已处理' },
          { value: '已忽略', label: '已忽略' },
          { value: '已放弃', label: '已放弃' }
        ],
        inputPlaceholder: '请选择处理状态',
        inputValidator: (value) => {
          if (!value) {
            return '处理状态不能为空';
          }
          return true;
        }
      }).then(({ value }) => {
        this.$prompt('请输入备注信息', '批量处理', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'textarea',
          inputPlaceholder: '请输入备注信息（可选）'
        }).then(({ value: beizhixinxi }) => {
          const ids = this.multipleSelection.map(item => item.id);
          batchHandle(ids, value, beizhixinxi || '').then(response => {
            if (response.code === 0) {
              this.$message({
                type: 'success',
                message: response.msg || '批量处理成功!'
              });
              this.getList();
            } else {
              this.$message({
                type: 'error',
                message: response.msg || '批量处理失败'
              });
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消批量处理'
          });          
        });
      });
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
    },
    parseTime
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}
.button-group {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>