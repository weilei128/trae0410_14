<template>
  <div class="books-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书列表</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索图书名称/作者/ISBN"
              style="width: 250px; margin-right: 10px"
              @keyup.enter="handleSearch"
              clearable
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="openAddDialog">添加图书</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="books" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="totalStock" label="总库存" width="80" align="center" />
        <el-table-column prop="availableStock" label="可借" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.availableStock > 0 ? 'success' : 'danger'">
              {{ row.availableStock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑图书' : '添加图书'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="书名" prop="name">
          <el-input v-model="form.name" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="总库存" prop="totalStock">
          <el-input-number v-model="form.totalStock" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bookApi } from '../api'

export default {
  name: 'BooksView',
  setup() {
    const books = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const submitting = ref(false)
    const formRef = ref(null)
    
    const form = reactive({
      id: null,
      name: '',
      author: '',
      isbn: '',
      category: '',
      totalStock: 1,
      availableStock: 1
    })
    
    const rules = {
      name: [{ required: true, message: '请输入书名', trigger: 'blur' }],
      totalStock: [{ required: true, message: '请输入库存', trigger: 'change' }]
    }
    
    const loadBooks = async () => {
      loading.value = true
      try {
        const res = await bookApi.list(searchKeyword.value)
        if (res.code === 200) {
          books.value = res.data || []
        } else {
          ElMessage.error(res.message || '加载失败')
        }
      } catch (error) {
        ElMessage.error('网络错误')
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      loadBooks()
    }
    
    const resetForm = () => {
      form.id = null
      form.name = ''
      form.author = ''
      form.isbn = ''
      form.category = ''
      form.totalStock = 1
      form.availableStock = 1
    }
    
    const openAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }
    
    const openEditDialog = (row) => {
      isEdit.value = true
      form.id = row.id
      form.name = row.name
      form.author = row.author
      form.isbn = row.isbn
      form.category = row.category
      form.totalStock = row.totalStock
      form.availableStock = row.availableStock
      dialogVisible.value = true
    }
    
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (!valid) return
        
        submitting.value = true
        try {
          let res
          if (isEdit.value) {
            res = await bookApi.update(form.id, form)
          } else {
            form.availableStock = form.totalStock
            res = await bookApi.add(form)
          }
          
          if (res.code === 200) {
            ElMessage.success(res.message || '操作成功')
            dialogVisible.value = false
            loadBooks()
          } else {
            ElMessage.error(res.message || '操作失败')
          }
        } catch (error) {
          ElMessage.error('网络错误')
        } finally {
          submitting.value = false
        }
      })
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这本图书吗？', '提示', {
          type: 'warning'
        })
        
        const res = await bookApi.delete(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadBooks()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('网络错误')
        }
      }
    }
    
    onMounted(() => {
      loadBooks()
    })
    
    return {
      books,
      loading,
      searchKeyword,
      dialogVisible,
      isEdit,
      submitting,
      formRef,
      form,
      rules,
      handleSearch,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.books-view {
  max-width: 1200px;
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
</style>
