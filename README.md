# 图书借阅管理系统

## 项目结构

```
basicPro1/
├── backend/                 # SpringBoot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/library/
│   │       │   ├── LibraryApplication.java    # 启动类
│   │       │   ├── config/                    # 配置类
│   │       │   │   └── CorsConfig.java        # 跨域配置
│   │       │   ├── controller/                # 控制器层
│   │       │   │   ├── BookController.java
│   │       │   │   └── BorrowController.java
│   │       │   ├── service/                   # 服务层
│   │       │   │   ├── BookService.java
│   │       │   │   └── BorrowService.java
│   │       │   ├── entity/                    # 实体类
│   │       │   │   ├── Book.java
│   │       │   │   └── BorrowRecord.java
│   │       │   ├── common/                    # 公共类
│   │       │   │   └── Result.java            # 统一返回结果
│   │       │   └── util/                      # 工具类
│   │       │       ├── JsonFileUtil.java
│   │       │       ├── DateUtil.java
│   │       │       └── IdGenerator.java
│   │       └── resources/
│   │           └── application.yml
│   ├── data/                                  # JSON 数据文件
│   │   ├── books.json
│   │   └── borrows.json
│   └── pom.xml
│
└── frontend/                # Vue3 前端
    ├── src/
    │   ├── main.js
    │   ├── App.vue
    │   ├── router/
    │   │   └── index.js
    │   ├── api/
    │   │   └── index.js
    │   └── views/
    │       ├── BooksView.vue      # 图书管理
    │       ├── BorrowsView.vue    # 借阅管理
    │       └── RecordsView.vue    # 借阅记录
    ├── index.html
    ├── vite.config.js
    └── package.json
```

## 启动方式

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5173

## 功能特性

### 已修复的 Bug

1. **借阅状态异常** - 使用明确的状态常量（0=借阅中, 1=已归还, 2=已逾期）
2. **重复借阅** - 在借阅前检查是否已有未归还的同本书
3. **跨域失败** - 配置了完整的 CORS 支持
4. **前端数据不刷新** - 每次操作后自动重新加载数据

### 代码重构

1. **Controller / Service 层分离** - 业务逻辑在 Service 层，Controller 只负责请求处理
2. **统一返回结果封装** - 使用 `Result<T>` 类统一 API 返回格式
3. **工具类提取** - `JsonFileUtil`、`DateUtil`、`IdGenerator`

### 功能列表

1. **图书管理**
   - 图书列表展示
   - 图书搜索（按名称/作者/ISBN/分类）
   - 添加图书
   - 编辑图书
   - 删除图书

2. **借阅管理**
   - 查看可借图书
   - 借阅图书（填写借阅人信息、借阅天数）
   - 查询个人借阅记录
   - 归还图书

3. **借阅记录**
   - 全部借阅记录列表
   - 按状态筛选（借阅中/已归还/已逾期）
   - 按关键词搜索
   - 查看逾期记录
   - 超时标记显示（红色高亮）
   - 统计信息展示

## API 接口

### 图书接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/books | 获取图书列表（支持 keyword 参数搜索） |
| GET | /api/books/{id} | 获取单个图书 |
| POST | /api/books | 添加图书 |
| PUT | /api/books/{id} | 更新图书 |
| DELETE | /api/books/{id} | 删除图书 |

### 借阅接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/borrows | 获取借阅记录（支持 keyword、status 参数） |
| GET | /api/borrows/{id} | 获取单条借阅记录 |
| GET | /api/borrows/overdue | 获取逾期记录 |
| POST | /api/borrows/borrow | 借阅图书 |
| POST | /api/borrows/return/{id} | 归还图书 |
