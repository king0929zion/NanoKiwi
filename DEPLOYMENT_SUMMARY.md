# 🎉 NanoWiki 项目部署总结

## ✅ 已完成的工作

### 1. 代码推送
- ✅ 代码已成功推送到：https://github.com/king0929zion/NanoKiwi
- ✅ 远程仓库已配置：`nanowiki` → https://github.com/king0929zion/NanoKiwi.git
- ✅ 主分支：`main`

### 2. GitHub Actions 工作流
- ✅ 已创建自动构建工作流：`.github/workflows/build_nanowiki_apk.yml`
- ✅ 支持多平台构建：arm, arm64, x86, x64
- ✅ 支持自动签名 APK
- ✅ 支持自动创建 Release（当推送 Tag 时）

### 3. 项目配置
- ✅ 更新了 `.gitignore`，忽略构建产物
- ✅ 添加了 `.gitattributes`，规范文件格式
- ✅ 创建了构建说明文档

## 📋 下一步操作

### 步骤 1: 配置 GitHub Secrets（可选但推荐）

为了使用你自己的签名密钥，需要在 GitHub 仓库中添加 Secrets：

1. 访问：https://github.com/king0929zion/NanoKiwi/settings/secrets/actions
2. 点击 "New repository secret"
3. 添加以下 Secrets：

   | Secret 名称 | 说明 | 示例值 |
   |------------|------|--------|
   | `KEYSTORE_BASE64` | 密钥库文件的 Base64 编码 | （见下方生成方法）|
   | `KEYSTORE_PASSWORD` | 密钥库密码 | `your_store_password` |
   | `KEY_PASSWORD` | 密钥密码 | `your_key_password` |
   | `KEY_ALIAS` | 密钥别名 | `nanowiki` |

#### 生成密钥和 Base64

```bash
# 1. 生成密钥
keytool -genkey -v \
  -keystore nanowiki-release.jks \
  -alias nanowiki \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=NanoWiki Browser, OU=Development, O=NanoWiki, C=CN"

# 2. 转换为 Base64（Windows PowerShell）
[Convert]::ToBase64String([IO.File]::ReadAllBytes("nanowiki-release.jks")) | Out-File -Encoding ASCII keystore-base64.txt

# 2. 转换为 Base64（Linux/Mac）
base64 -i nanowiki-release.jks -o keystore-base64.txt

# 3. 复制 keystore-base64.txt 的内容到 GitHub Secret
```

### 步骤 2: 触发构建

#### 方式 1: 自动触发（Push 代码）
```bash
git push nanowiki kiwi:main
```

#### 方式 2: 手动触发
1. 访问：https://github.com/king0929zion/NanoKiwi/actions
2. 选择 "Build NanoWiki APK" 工作流
3. 点击 "Run workflow"
4. 选择平台（arm64 推荐）
5. 点击 "Run workflow"

#### 方式 3: 创建 Tag 触发 Release
```bash
git tag v1.0.0
git push nanowiki v1.0.0
```

### 步骤 3: 监控构建

1. 访问 Actions 页面：https://github.com/king0929zion/NanoKiwi/actions
2. 查看构建进度
3. 构建完成后：
   - 下载 APK：在 Artifacts 中下载
   - 如果创建了 Tag，会在 Releases 中自动创建 Release

## ⚠️ 重要提示

### Chromium 构建的限制

GitHub Actions 免费版可能无法完成完整的 Chromium 构建，因为：

1. **构建时间**: 需要 2-6 小时（可能超时）
2. **资源限制**: 
   - 6 小时超时限制
   - 14GB RAM
   - 有限的磁盘空间
3. **网络要求**: 需要下载大量依赖（几十GB）

### 推荐方案

#### 方案 A: 使用自托管 GitHub Runner（最佳）
- 在自己的服务器上运行 GitHub Runner
- 无时间限制
- 可以配置更多资源

#### 方案 B: 本地构建（最可靠）
- 在自己的机器上构建
- 完全控制构建环境
- 参考 `BUILD_INSTRUCTIONS.md`

#### 方案 C: 使用云构建服务
- Google Cloud Build
- Azure DevOps
- CircleCI（付费版）

#### 方案 D: 简化构建（快速测试）
- 只构建 Wiki 扩展部分
- 使用预编译的 Chromium
- 参考 `BUILD_INSTRUCTIONS.md` 中的方案 3

## 📦 构建产物

构建成功后，APK 文件会：
- 上传到 GitHub Actions Artifacts（保留 30 天）
- 如果创建了 Tag，会自动创建 GitHub Release

### APK 命名格式
```
NanoWiki-{platform}-v{run_number}-signed.apk
```

例如：
- `NanoWiki-arm64-v123-signed.apk`
- `NanoWiki-arm-v123-signed.apk`

## 🔍 故障排除

### 构建失败

1. **检查日志**: 在 Actions 页面查看详细日志
2. **常见问题**:
   - 超时：增加 timeout-minutes（已设置为 360 分钟）
   - 内存不足：使用自托管 Runner
   - 依赖下载失败：检查网络连接

### 签名失败

1. 确认 Secrets 配置正确
2. 检查密钥格式
3. 验证密码是否正确

### APK 找不到

1. 检查构建是否成功完成
2. 查看构建日志中的输出路径
3. 确认 args.gn 配置正确

## 📚 相关文档

- `BUILD_INSTRUCTIONS.md` - 详细的构建说明
- `SETUP_GITHUB.md` - GitHub 设置指南
- `.github/workflows/build_nanowiki_apk.yml` - 工作流配置

## 🎯 快速开始

1. **首次推送**（已完成）:
   ```bash
   git push nanowiki kiwi:main
   ```

2. **配置签名密钥**（可选）:
   - 按照上方步骤生成密钥
   - 添加到 GitHub Secrets

3. **触发构建**:
   - 访问 Actions 页面手动触发
   - 或推送新代码自动触发

4. **下载 APK**:
   - 构建完成后在 Artifacts 中下载

## 📞 支持

如果遇到问题：
1. 查看构建日志
2. 参考 `BUILD_INSTRUCTIONS.md`
3. 检查 GitHub Actions 文档

---

**仓库地址**: https://github.com/king0929zion/NanoKiwi  
**Actions 页面**: https://github.com/king0929zion/NanoKiwi/actions

