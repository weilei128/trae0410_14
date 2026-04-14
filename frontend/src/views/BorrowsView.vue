<template>
  <div class="borrows-view">
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>可借图书</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索图书"
                style="width: 200px"
                @keyup.enter="loadBooks"
                clearable
              >
                <template #append>
                  <el-button @click="loadBooks">搜索</el-button>
                </template>
              </el-input>
            </div>
          </template>
          
          <el-table :data="books" v-loading="loading" stripe max-height="500">
            <el-table-column prop="name" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="availableStock" label="可借" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.availableStock > 0 ? 'success' : 'danger'" size="small">
                  {{ row.availableStock }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button 
                  size="small" 
                  type="primary" 
                  :disabled="row.availableStock <= 0"
                  @click="openBorrowDialog(row)"
                >
                  借阅
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>我的借阅</span>
          </template>
          
          <el-form :inline="true" style="margin-bottom: 15px">
            <el-form-item label="借阅人">
              <el-input v-model="borrowerName" placeholder="输入姓名" style="width: 150px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadMyBorrows">查询</el-button>
            </el-form-item>
          </el-form>
          
          <el-table :data="myBorrows" v-loading="myLoading" stripe max-height="400">
            <el-table-column prop="bookName" label="书名" min-width="120" />
            <el-table-column prop="borrowDate" label="借阅日期" width="100" />
            <el-table-column prop="dueDate" label="应还日期" width="100">
              <template #default="{ row }">
                <span :class="{ 'overdue-text': row.overdue && row.status !== 1 }">
                  {{ row.dueDate }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row)" size="small">
                  {{ getStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70">
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
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="borrowDialogVisible" title="借阅图书" width="400px">
      <el-form :model="borrowForm" :rules="borrowRules" ref="borrowFormRef" label-width="80px">
        <el-form-item label="图书">
          <el-input :value="selectedBook?.name" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="borrowerName">
          <el-input v-model="borrowForm.borrowerName" placeholder="请输入借阅人姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="borrowerPhone">
          <el-input v-model="borrowForm.borrowerPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number v-model="borrowForm.days" :min="1" :max="90" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBorrow" :loading="borrowing">确定借阅</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bookApi, borrowApi } from '../api'

export default {
  name: 'BorrowsView',
  setup() {
    const books = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')
    
    const myBorrows = ref([])
    const myLoading = ref(false)
    const borrowerName = ref('')
    
    const borrowDialogVisible = ref(false)
    const selectedBook = ref(null)
    const borrowing = ref(false)
    const borrowFormRef = ref(null)
    
    const borrowForm = reactive({
      bookId: null,
      borrowerName: '',
      borrowerPhone: '',
      days: 30
    })
    
    const borrowRules = {
      borrowerName: [{ required: true, message: '请输入借阅人姓名', trigger: 'blur' }]
    }
    
    const loadBooks = async () => {
      loading.value = true
      try {
        const res = await bookApi.list(searchKeyword.value)
        if (res.code === 200) {
          books.value = res.data || []
        }
      } catch (error) {
        ElMessage.error('加载图书失败')
      } finally {
        loading.value = false
      }
    }
    
    const loadMyBorrows = async () => {
      if (!borrowerName.value.trim()) {
        ElMessage.warning('请输入借阅人姓名')
        return
      }
      
      myLoading.value = true
      try {
        const res = await borrowApi.list(borrowerName.value)
        if (res.code === 200) {
          myBorrows.value = res.data || []
        }
      } catch (error) {
        ElMessage.error('加载借阅记录失败')
      } finally {
        myLoading.value = false
      }
    }
    
    const openBorrowDialog = (book) => {
      selectedBook.value = book
      borrowForm.bookId = book.id
      borrowForm.borrowerName = borrowerName.value
      borrowForm.borrowerPhone = ''
      borrowForm.days = 30
      borrowDialogVisible.value = true
    }
    
    const handleBorrow = async () => {
      if (!borrowFormRef.value) return
      
      await borrowFormRef.value.validate(async (valid) => {
        if (!valid) return
        
        borrowing.value = true
        try {
          const res = await borrowApi.borrow(borrowForm)
          if (res.code === 200) {
            ElMessage.success('借阅成功')
            borrowDialogVisible.value = false
            loadBooks()
            if (borrowerName.value) {
              loadMyBorrows()
            }
          } else {
            ElMessage.error(res.message || '借阅失败')
          }
        } catch (error) {
          ElMessage.error('网络错误')
        } finally {
          borrowing.value = false
        }
      })
    }
    
    const handleReturn = async (row) => {
      try {
        await ElMessageBox.confirm('确定要归还这本图书吗？', '提示', {
          type: 'warning'
        })
        
        const res = await borrowApi.returnBook(row.id)
        if (res.code === 200) {
          ElMessage.success('归还成功')
          loadBooks()
          loadMyBorrows()
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
      loadBooks()
    })
    
    return {
      books,
      loading,
      searchKeyword,
      myBorrows,
      myLoading,
      borrowerName,
      borrowDialogVisible,
      selectedBook,
      borrowing,
      borrowFormRef,
      borrowForm,
      borrowRules,
      loadBooks,
      loadMyBorrows,
      openBorrowDialog,
      handleBorrow,
      handleReturn,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
.borrows-view {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
