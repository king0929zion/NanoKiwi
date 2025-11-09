# GitHub 仓库设置和自动构建指南

## 📋 前置要求

1. GitHub 账户已创建仓库：https://github.com/king0929zion/NanoKiwi
2. 本地代码已准备好推送
3. （可选）准备 APK 签名密钥

## 🚀 步骤 1: 推送代码到 GitHub

### 1.1 提交所有更改

```bash
# 添加所有更改
git add -A

# 提交更改
git commit -m "feat: 将 Kiwi Browser 改造为 NanoWiki，集成 Wiki AI 助手

- 重命名浏览器为 NanoWiki
- 集成 Wiki AI 助手（原 nanobrowser）
- 优化 UI 为 Anthropic/Claude 风格
- 移除推广内容
- 添加 AI 执行任务时的光效和暂停功能
- 优化移动端适配"

# 推送到新仓库
git push nanowiki kiwi:main
```

### 1.2 如果仓库为空，直接推送

```bash
# 如果远程仓库是空的，可以直接推送
git push nanowiki kiwi:main --force
```

## 🔐 步骤 2: 配置 APK 签名密钥（可选但推荐）

### 2.1 生成签名密钥

```bash
keytool -genkey -v \
  -keystore nanowiki-release.jks \
  -alias nanowiki \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=NanoWiki Browser, OU=Development, O=NanoWiki, L=City, ST=State, C=CN"
```

### 2.2 将密钥转换为 Base64

```bash
# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("nanowiki-release.jks")) | Out-File -Encoding ASCII keystore-base64.txt

# Linux/Mac
base64 -i nanowiki-release.jks -o keystore-base64.txt
```

### 2.3 在 GitHub 仓库中添加 Secrets

1. 进入仓库：https://github.com/king0929zion/NanoKiwi
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 添加以下 Secrets：

   - `KEYSTORE_BASE64`: 密钥库文件的 Base64 编码（从 keystore-base64.txt 复制）
   - `KEYSTORE_PASSWORD`: 密钥库密码
   - `KEY_PASSWORD`: 密钥密码
   - `KEY_ALIAS`: 密钥别名（通常是 `nanowiki`）

## ⚙️ 步骤 3: GitHub Actions 工作流说明

### 3.1 自动触发条件

- **Push 到 main/master/kiwi 分支**: 自动构建
- **创建 Tag (v*)**: 自动构建并创建 Release
- **Pull Request**: 自动构建测试
- **手动触发**: 在 Actions 页面手动运行

### 3.2 构建平台

默认构建 `arm64` 平台，可以通过 workflow_dispatch 手动选择：
- `arm`: 32位 ARM 设备
- `arm64`: 64位 ARM 设备（推荐）
- `x86`: 32位 x86 设备
- `x64`: 64位 x86 设备

### 3.3 构建产物

- APK 文件会上传到 GitHub Actions Artifacts
- 如果创建了 Tag，会自动创建 GitHub Release
- Artifacts 保留 30 天

## 📝 步骤 4: 首次推送

执行以下命令推送代码：

```bash
# 确保所有更改已提交
git add -A
git commit -m "Initial commit: NanoWiki Browser with Wiki AI"

# 推送到 GitHub
git push nanowiki kiwi:main
```

## 🔍 步骤 5: 监控构建

1. 访问：https://github.com/king0929zion/NanoKiwi/actions
2. 查看构建进度
3. 构建完成后下载 APK

## ⚠️ 注意事项

### Chromium 构建的特殊性

Chromium 项目构建非常复杂，GitHub Actions 构建可能遇到以下问题：

1. **构建时间**: 可能需要 2-6 小时
2. **磁盘空间**: 需要大量空间（建议使用自托管 runner）
3. **内存要求**: 至少 16GB RAM
4. **网络要求**: 需要下载大量依赖

### 替代方案

如果 GitHub Actions 构建失败，可以考虑：

1. **使用本地构建**:
   ```bash
   # 在本地构建 APK
   gn gen out/Default --args='target_os="android" target_cpu="arm64"'
   autoninja -C out/Default chrome_public_apk
   ```

2. **使用云构建服务**:
   - Google Cloud Build
   - Azure DevOps
   - CircleCI（支持更大的资源）

3. **简化构建流程**:
   - 只构建 arm64 版本
   - 使用预编译的 Chromium 库
   - 使用 Gradle 构建系统

## 🛠️ 故障排除

### 构建失败

1. 检查 Actions 日志
2. 确认所有依赖已安装
3. 检查磁盘空间是否充足
4. 尝试增加 timeout 时间

### 签名失败

1. 确认 Secrets 配置正确
2. 检查密钥格式是否正确
3. 验证密钥密码是否正确

### APK 找不到

1. 检查构建是否成功完成
2. 查看构建日志中的输出路径
3. 确认 args.gn 配置正确

## 📚 相关资源

- [Chromium 构建文档](https://chromium.googlesource.com/chromium/src/+/main/docs/android_build_instructions.md)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Android APK 签名指南](https://developer.android.com/studio/publish/app-signing)

