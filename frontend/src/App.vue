<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>图书借阅管理系统</h1>
        <el-menu mode="horizontal" :default-active="activeMenu" router>
          <el-menu-item index="/books">图书管理</el-menu-item>
          <el-menu-item index="/borrows">借阅管理</el-menu-item>
          <el-menu-item index="/records">借阅记录</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const route = useRoute()
    const activeMenu = computed(() => route.path)
    return { activeMenu }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.el-header h1 {
  font-size: 20px;
  font-weight: normal;
}

.el-header .el-menu {
  background-color: transparent;
  border-bottom: none;
}

.el-header .el-menu-item {
  color: white !important;
  border-bottom: none !important;
}

.el-header .el-menu-item:hover,
.el-header .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.2) !important;
}

.el-main {
  padding: 20px;
}
</style>
