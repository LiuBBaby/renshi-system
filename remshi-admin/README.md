# 项目简介

本项目基于 [RuoYi v3.8.9](https://gitee.com/y_project/RuoYi-Vue) 前后端分离快速开发框架，后端采用 Spring Boot，前端采用 Vue + Element UI，适合企业级管理系统开发。

## 目录结构

```
├── ruoyi-admin      # 后端主模块，Spring Boot 启动入口
├── ruoyi-common     # 公共模块，工具类、通用代码
├── ruoyi-framework  # 框架核心模块，安全、权限等
├── ruoyi-generator  # 代码生成模块
├── ruoyi-quartz     # 定时任务模块
├── ruoyi-system     # 系统业务模块
├── ruoyi-ui         # 前端 Vue 项目
```

## 技术栈
- 后端：Spring Boot、Spring Security、MyBatis、Redis、Quartz
- 前端：Vue、Element UI、Axios、Vue Router、Vuex
- 其他：JWT、代码生成器、在线构建器

## 快速启动

### 后端启动
1. 安装 JDK 8+、Maven 3.6+
2. 配置数据库（MySQL 5.7/8.0），修改 `ruoyi-admin/src/main/resources/application-druid.yml` 数据库连接信息
3. 在项目根目录执行：
   ```bash
   mvn clean package
   cd ruoyi-admin
   java -jar target/ruoyi-admin.jar
   ```

### 前端启动
1. 安装 Node.js 14+
2. 进入 `ruoyi-ui` 目录，执行：
   ```bash
   npm install
   npm run dev
   ```

### 默认账号
- 用户名：admin
- 密码：admin123

## 主要功能
- 用户、角色、菜单、部门、岗位、字典、参数、公告、日志、在线用户、定时任务、代码生成、服务监控、缓存监控、在线构建器、数据库连接池监控等

## 文档与演示
- 在线演示：http://vue.ruoyi.vip
- 官方文档：http://doc.ruoyi.vip

## 版权说明
本项目遵循 [MIT License](./LICENSE)，可免费商用。

---

> 本 README 已根据本地实际项目结构优化，便于开发和部署。

