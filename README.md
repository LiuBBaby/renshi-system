# 人事考勤管理系统

## 项目简介

人事考勤管理系统是一个基于RuoYi框架开发的全功能企业级人事和考勤管理平台，采用前后端分离架构，提供Web管理端和移动端小程序双端支持。系统包含员工信息管理、考勤打卡、请假审批、出差申请、月度报表等核心功能，为企业人事管理提供全面的解决方案。

## 系统架构

系统分为三个主要部分：

1. **后台管理系统(remshi-admin)** - 基于RuoYi-Vue框架的管理端
2. **移动端小程序(renshi-minapp)** - 基于UniApp开发的员工移动端
3. **数据库** - MySQL数据库存储系统数据

### 技术栈

#### 后端技术栈
- Spring Boot 2.5.15 - 主框架
- Spring Security - 安全框架
- MyBatis - ORM框架
- Redis - 缓存
- JWT - 认证授权
- Quartz - 任务调度
- Druid - 数据库连接池
- Swagger - API文档

#### 前端技术栈(管理端)
- Vue.js - 前端框架
- Element UI - UI组件库
- Axios - HTTP客户端
- Vue Router - 路由管理
- Vuex - 状态管理

#### 移动端技术栈
- UniApp - 跨平台开发框架
- uni-ui - UI组件库

## 目录结构

```
├── remshi-admin/         # 后台管理系统
│   ├── ruoyi-admin/      # 后端主模块，Spring Boot 启动入口
│   ├── ruoyi-common/     # 公共模块，工具类、通用代码
│   ├── ruoyi-framework/  # 框架核心模块，安全、权限等
│   ├── ruoyi-generator/  # 代码生成模块
│   ├── ruoyi-quartz/     # 定时任务模块
│   ├── ruoyi-system/     # 系统业务模块
│   └── ruoyi-ui/         # 前端 Vue 项目
│
├── renshi-minapp/        # 移动端小程序
│   ├── api/              # API接口
│   ├── components/       # 组件
│   ├── pages/            # 页面
│   ├── static/           # 静态资源
│   └── utils/            # 工具类
│
└── ruoyi.sql             # 数据库脚本
```

## 核心功能

### 基础功能
- 用户、角色、菜单权限管理
- 部门、岗位管理
- 字典、参数管理
- 操作日志、登录日志

### 人事管理功能
- 员工信息管理 - 维护员工基本信息、部门归属、岗位等
- 考勤管理 - 考勤规则设置、考勤位置管理
- 请假管理 - 请假申请、审批流程
- 出差管理 - 出差申请、审批流程
- 考勤统计 - 月度考勤统计、报表生成

### 移动端功能
- 员工打卡 - 基于地理位置的移动端打卡
- 请假申请 - 手机端提交请假申请
- 出差申请 - 手机端提交出差申请
- 审批处理 - 管理人员移动端审批

## 快速开始

### 环境准备
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Node.js 14+
- Redis

### 数据库配置
1. 创建数据库 `ruoyi`
2. 执行 `ruoyi.sql` 脚本

### 后端运行
1. 修改数据库连接信息
   - 文件路径: `remshi-admin/ruoyi-admin/src/main/resources/application-druid.yml`
   - 配置数据库连接、用户名和密码

2. 启动后端服务
```bash
cd remshi-admin
mvn clean package
cd ruoyi-admin/target
java -jar ruoyi-admin.jar
```

### 管理端前端运行
```bash
cd remshi-admin/ruoyi-ui
npm install
npm run dev
```

### 移动端运行
1. 修改API地址配置
   - 文件路径: `renshi-minapp/config.js`
   - 配置正确的后端API地址

2. 使用HBuilderX导入项目
3. 运行到小程序开发者工具或模拟器

### 默认账号
- 管理员账号: admin
- 管理员密码: admin123

## 主要数据表说明

- `emp_info` - 员工信息表
- `attendance_info` - 考勤记录表
- `attendance_config` - 考勤规则配置表
- `attendance_location` - 考勤打卡位置表
- `attendance_monthly` - 考勤月度统计表
- `business_trip_info` - 出差申请表

## 系统截图

待补充

## 开发与贡献

欢迎贡献代码或提交问题，请遵循以下流程：
1. Fork 仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add feature xxx'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建Pull Request

## 许可证

本项目基于 [MIT License](./LICENSE) 开源，可免费商用。

## 致谢

本项目基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 和 [RuoYi-App](https://gitee.com/y_project/RuoYi-App) 进行开发，特此感谢。 