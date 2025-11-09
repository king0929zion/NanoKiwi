# NanoWiki Browser

NanoWiki 是基于 Chromium 的移动浏览器，内置原生 AI 助手 Wiki，提供智能网页自动化功能。

## 主要特性

### 原生 AI 支持
- **Wiki AI 助手**：内置的 AI 侧边栏，提供智能网页自动化功能
- **Anthropic/Claude 风格 UI**：采用温暖、中性的配色方案，简洁优雅的界面设计
- **移动端优化**：完全适配移动端的对话界面，提供流畅的用户体验
- **AI 执行光效**：AI 执行任务时屏幕四边显示柔和光效，底部悬浮暂停按钮

### 浏览器功能
- 基于 Chromium 内核，支持完整的网页浏览体验
- 支持扩展程序安装
- 隐私保护和广告拦截
- 夜间模式、翻译等实用功能

## Wiki AI 助手

Wiki 是 NanoWiki 浏览器内置的原生 AI 助手，可以帮助用户：
- 自动化网页任务
- 采集和整理数据
- 执行复杂的浏览流程
- 提供智能建议和帮助

### UI 设计
Wiki 的界面设计完全参考 Anthropic/Claude 的风格：
- **配色方案**：暖色调、中性色，无渐变设计
- **圆角设计**：统一的圆角半径，简洁美观
- **深色模式**：完整的深色模式支持
- **无推广内容**：界面专注于功能本身，无任何推广信息

### 使用方法
1. 点击工具栏的 Wiki 图标打开侧边栏
2. 首次使用时配置 API key（支持 Anthropic、OpenAI、Gemini、Ollama 等）
3. 输入任务描述，Wiki 会自动执行并反馈结果
4. AI 执行任务时，屏幕会显示光效，可随时点击底部暂停按钮

## 开发说明

### 项目结构
- `chrome/` - Chromium 浏览器核心代码
- `nanobrowser/` - Wiki AI 助手扩展代码
- `chrome/android/` - Android 平台相关代码

### 构建要求
- Android SDK 33+
- Chromium 构建工具链（depot_tools）
- Java 17+
- Node.js 22+ 和 pnpm（用于 Wiki 扩展开发）
- 至少 100GB 磁盘空间（完整构建）

### 本地构建

1. **安装依赖**
   ```bash
   git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
   export PATH="$PWD/depot_tools:$PATH"
   ```

2. **配置构建**
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
   android_channel = "stable"
   EOF
   gn gen out/Default
   ```

3. **构建 APK**
   ```bash
   autoninja -C out/Default chrome_public_apk
   ```

4. **签名 APK**
   ```bash
   keytool -genkey -v -keystore keystore.jks -alias nanowiki -keyalg RSA -keysize 2048 -validity 10000
   jarsigner -keystore keystore.jks out/Default/apks/ChromePublic.apk nanowiki
   zipalign -v 4 out/Default/apks/ChromePublic.apk NanoWiki-signed.apk
   ```

### GitHub Actions 自动构建

项目已配置 GitHub Actions 自动构建工作流（`.github/workflows/build_nanowiki_apk.yml`）。

**触发方式**：
- **所有分支推送**：每次推送代码都会自动触发完整构建
- 创建 Tag (v*)：自动构建并创建 Release
- Pull Request：推送到 main/master/kiwi 分支的 PR 会触发构建
- 手动触发：在 Actions 页面手动运行，可选择目标平台

**构建流程**：
1. 同步 Chromium 源码（约 30-60 分钟，自动重试机制）
2. 应用自定义修改
3. 生成构建配置
4. 编译 APK（约 2-6 小时）
5. 签名和打包
6. 上传构建产物

**配置签名密钥**（可选）：
1. 生成密钥：`keytool -genkey -v -keystore keystore.jks -alias nanowiki ...`
2. 转换为 base64：`base64 -i keystore.jks`
3. 在 GitHub Secrets 中添加：
   - `KEYSTORE_BASE64`: 密钥库 base64
   - `KEYSTORE_PASSWORD`: 密钥库密码
   - `KEY_PASSWORD`: 密钥密码
   - `KEY_ALIAS`: 密钥别名（默认: nanowiki）

**注意事项**：
- Chromium 完整构建需要大量时间和资源（源码同步 30-60 分钟，构建 2-6 小时）
- GitHub Actions 免费版单次运行最长 6 小时，可能无法完成完整构建
- 建议使用自托管 Runner 或本地构建以获得更好的性能
- 构建过程包含自动重试机制，提高成功率

### Wiki 扩展独立构建

```bash
cd nanobrowser
pnpm install
pnpm build
```

构建产物在 `nanobrowser/chrome-extension/dist/` 目录。

## 最近更新

- ✅ 将浏览器名称从 Kiwi Browser 更改为 NanoWiki
- ✅ 将 nanobrowser 插件名称更改为 Wiki
- ✅ 优化 Wiki UI，采用 Anthropic/Claude 风格设计
- ✅ 移除所有推广内容（GitHub、Discord 等）
- ✅ 添加 AI 执行任务时的光效和暂停功能
- ✅ 优化移动端适配
- ✅ 配置 GitHub Actions 自动构建和签名
- ✅ 优化构建流程：所有分支推送自动触发构建
- ✅ 改进 Chromium 同步：添加重试机制和错误处理
- ✅ 修复代码检查问题：通过所有 linter 检查

## 许可证

本项目基于 Apache 2.0 许可证开源。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进 NanoWiki 浏览器。

---

Copyright © 2025 NanoWiki.
