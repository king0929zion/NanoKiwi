# NanoWiki 构建说明

## ⚠️ 重要提示

Chromium 项目的完整构建非常复杂，GitHub Actions 的免费版可能无法完成完整构建（需要大量时间和资源）。

## 🚀 推荐的构建方式

### 方案 1: 本地构建（推荐）

#### 前置要求
- Linux 或 macOS 系统（Windows 需要 WSL2）
- 至少 100GB 可用磁盘空间
- 16GB+ RAM
- 稳定的网络连接

#### 构建步骤

1. **安装 depot_tools**
   ```bash
   git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
   export PATH="$PWD/depot_tools:$PATH"
   ```

2. **获取源码**
   ```bash
   git clone https://github.com/king0929zion/NanoKiwi.git
   cd NanoKiwi
   ```

3. **安装依赖**
   ```bash
   # Linux
   ./build/install-build-deps.sh
   
   # macOS
   xcode-select --install
   ```

4. **配置构建参数**
   ```bash
   mkdir -p out/Default
   cat > out/Default/args.gn << EOF
   target_os = "android"
   target_cpu = "arm64"
   is_debug = false
   is_official_build = true
   symbol_level = 1
   enable_nacl = false
   enable_remoting = false
   enable_webview = false
   is_component_build = false
   android_channel = "stable"
   android_default_version_name = "1.0.0"
   android_default_version_code = "1000000"
   EOF
   ```

5. **生成构建文件**
   ```bash
   gn gen out/Default
   ```

6. **构建 APK**
   ```bash
   autoninja -C out/Default chrome_public_apk
   ```

7. **签名 APK**
   ```bash
   # 生成密钥（首次）
   keytool -genkey -v \
     -keystore nanowiki-release.jks \
     -alias nanowiki \
     -keyalg RSA \
     -keysize 2048 \
     -validity 10000 \
     -storepass YOUR_PASSWORD \
     -keypass YOUR_PASSWORD \
     -dname "CN=NanoWiki Browser, OU=Development, O=NanoWiki, C=CN"
   
   # 签名
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore nanowiki-release.jks \
     -storepass YOUR_PASSWORD \
     -keypass YOUR_PASSWORD \
     out/Default/apks/ChromePublic.apk \
     nanowiki
   
   # 对齐
   zipalign -v 4 out/Default/apks/ChromePublic.apk NanoWiki-signed.apk
   ```

### 方案 2: 使用 GitHub Actions（需要自托管 Runner）

GitHub Actions 的免费版限制：
- 6 小时超时
- 14GB RAM
- 有限的磁盘空间

**建议**: 使用自托管 GitHub Runner 或云构建服务。

### 方案 3: 使用预编译 Chromium（快速但功能受限）

如果只需要测试 Wiki 扩展功能，可以：

1. 下载预编译的 Chromium APK
2. 使用 `chrome://extensions` 加载 Wiki 扩展
3. 测试功能

## 📦 构建 Wiki 扩展（独立）

Wiki 扩展可以独立构建和测试：

```bash
cd nanobrowser
pnpm install
pnpm build
```

构建产物在 `nanobrowser/chrome-extension/dist/` 目录。

## 🔧 GitHub Actions 配置说明

### 已创建的 Workflow

文件：`.github/workflows/build_nanowiki_apk.yml`

### 触发方式

1. **自动触发**: Push 到 main/master/kiwi 分支
2. **Tag 触发**: 创建 v* 标签时自动构建并发布 Release
3. **手动触发**: 在 Actions 页面手动运行

### 配置签名密钥（可选）

1. 生成密钥（见上方步骤）
2. 转换为 Base64:
   ```bash
   base64 -i nanowiki-release.jks > keystore-base64.txt
   ```
3. 在 GitHub 仓库添加 Secrets:
   - `KEYSTORE_BASE64`: Base64 编码的密钥库
   - `KEYSTORE_PASSWORD`: 密钥库密码
   - `KEY_PASSWORD`: 密钥密码
   - `KEY_ALIAS`: 密钥别名（默认: nanowiki）

## 📝 构建优化建议

1. **使用 ccache**: 加速后续构建
2. **只构建 arm64**: 减少构建时间
3. **使用 Goma**: 分布式构建（需要 Google 账户）
4. **增量构建**: 只构建变更的部分

## 🐛 常见问题

### Q: GitHub Actions 构建超时
A: Chromium 构建需要很长时间，建议使用自托管 Runner 或本地构建。

### Q: 构建失败，提示缺少依赖
A: 检查 `build/install-build-deps.sh` 是否已运行，确保所有系统依赖已安装。

### Q: APK 签名失败
A: 确认密钥格式正确，密码匹配，别名正确。

### Q: 构建产物太大
A: 使用 `is_component_build = false` 和 `symbol_level = 1` 可以减少体积。

## 📚 参考资源

- [Chromium Android 构建文档](https://chromium.googlesource.com/chromium/src/+/main/docs/android_build_instructions.md)
- [Android APK 签名指南](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions 文档](https://docs.github.com/en/actions)

