# 项目上传指南

由于项目文件较大，需要通过Git命令行完成完整项目的上传。以下是上传步骤：

## 前提条件

1. 安装Git：https://git-scm.com/downloads
2. 拥有GitHub账号和权限

## 上传步骤

1. 克隆仓库到本地
```bash
git clone https://github.com/LiuBBaby/renshi-system.git
```

2. 进入项目目录
```bash
cd renshi-system
```

3. 复制项目文件到仓库目录
   - 将您的`remshi-admin`目录复制到仓库根目录
   - 将您的`renshi-minapp`目录复制到仓库根目录
   - 将您的`ruoyi.sql`文件复制到仓库根目录

4. 移除任何子目录中的`.git`文件夹（如果存在）
```bash
# Windows
Remove-Item -Path "remshi-admin/.git" -Recurse -Force
Remove-Item -Path "renshi-minapp/.git" -Recurse -Force

# Linux/MacOS
rm -rf remshi-admin/.git
rm -rf renshi-minapp/.git
```

5. 添加所有文件到Git
```bash
git add .
```

6. 提交更改
```bash
git commit -m "上传完整项目代码"
```

7. 推送到GitHub
```bash
git push origin main
```

## 大文件处理

如果遇到文件过大无法上传的情况，可以考虑以下方法：

1. 使用Git LFS（Large File Storage）处理大文件
2. 分批提交代码
3. 移除不必要的大文件（如编译产物、node_modules等）

## 网络问题

如果遇到网络连接问题，可以尝试：

1. 使用VPN或代理
2. 更改网络环境
3. 使用GitHub Desktop客户端代替命令行
4. 增加Git操作超时时间：
   ```bash
   git config --global http.postBuffer 524288000
   git config --global http.lowSpeedLimit 0
   git config --global http.lowSpeedTime 999999
   ```