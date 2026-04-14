<template>
  <div class="records-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>借阅记录</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索书名/借阅人/电话"
              style="width: 200px; margin-right: 10px"
              @keyup.enter="loadRecords"
              clearable
            />
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px" clearable>
              <el-option label="全部" :value="null" />
              <el-option label="借阅中" :value="0" />
              <el-option label="已归还" :value="1" />
              <el-option label="已逾期" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadRecords">搜索</el-button>
            <el-button type="warning" @click="loadOverdueRecords">查看逾期</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bookName" label="书名" min-width="150" />
        <el-table-column prop="borrowerName" label="借阅人" width="100" />
        <el-table-column prop="borrowerPhone" label="电话" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="100" />
        <el-table-column prop="dueDate" label="应还日期" width="100">
          <template #default="{ row }">
            <span :class="{ 'overdue-text': row.overdue && row.status !== 1 }">
              {{ row.dueDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="100">
          <template #default="{ row }">
            {{ row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="超时" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.overdue && row.status !== 1" type="danger" effect="dark">
              超时
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status !== 1"
              size="small" 
              type="warning"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="stats-bar">
        <el-tag type="info">总记录: {{ records.length }}</el-tag>
        <el-tag type="warning">借阅中: {{ borrowingCount }}</el-tag>
        <el-tag type="success">已归还: {{ returnedCount }}</el-tag>
        <el-tag type="danger">已逾期: {{ overdueCount }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { borrowApi } from '../api'

export default {
  name: 'RecordsView',
  setup() {
    const records = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')
    const statusFilter = ref(null)
    
    const borrowingCount = computed(() => {
      return records.value.filter(r => r.status === 0 && !r.overdue).length
    })
    
    const returnedCount = computed(() => {
      return records.value.filter(r => r.status === 1).length
    })
    
    const overdueCount = computed(() => {
      return records.value.filter(r => r.overdue && r.status !== 1).length
    })
    
    const loadRecords = async () => {
      loading.value = true
      try {
        const res = await borrowApi.list(searchKeyword.value, statusFilter.value)
        if (res.code === 200) {
          records.value = res.data || []
        } else {
          ElMessage.error(res.message || '加载失败')
        }
      } catch (error) {
        ElMessage.error('网络错误')
      } finally {
        loading.value = false
      }
    }
    
    const loadOverdueRecords = async () => {
      loading.value = true
      try {
        const res = await borrowApi.getOverdue()
        if (res.code === 200) {
          records.value = res.data || []
          ElMessage.success(`共有 ${records.value.length} 条逾期记录`)
        } else {
          ElMessage.error(res.message || '加载失败')
        }
      } catch (error) {
        ElMessage.error('网络错误')
      } finally {
        loading.value = false
      }
    }
    
    const handleReturn = async (row) => {
      try {
        await ElMessageBox.confirm('确定要归还这本图书吗？', '提示', {
          type: 'warning'
        })
        
        const res = await borrowApi.returnBook(row.id)
        if (res.code === 200) {
          ElMessage.success('归还成功')
          loadRecords()
        } else {
          ElMessage.error(res.message || '归还失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('网络错误')
        }
      }
    }
    
    const getStatusType = (row) => {
      if (row.status === 1) return 'success'
      if (row.overdue) return 'danger'
      return 'warning'
    }
    
    const getStatusText = (row) => {
      if (row.status === 1) return '已归还'
      if (row.overdue) return '已逾期'
      return '借阅中'
    }
    
    onMounted(() => {
      loadRecords()
    })
    
    return {
      records,
      loading,
      searchKeyword,
      statusFilter,
      borrowingCount,
      returnedCount,
      overdueCount,
      loadRecords,
      loadOverdueRecords,
      handleReturn,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
.records-view {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}

.stats-bar {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
</style>
