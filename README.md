# NanoWiki Browser

NanoWiki 是基于 Chromium 的移动浏览器，内置原生 AI 助手 Wiki，提供智能网页自动化功能。

## 主要特性

### 原生 AI 支持
- **Wiki AI 助手**：内置的 AI 侧边栏，提供智能网页自动化功能
- **Anthropic/Claude 风格 UI**：采用温暖、中性的配色方案，简洁优雅的界面设计
- **移动端优化**：完全适配移动端的对话界面，提供流畅的用户体验

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

## 开发说明

### 项目结构
- `chrome/` - Chromium 浏览器核心代码
- `nanobrowser/` - Wiki AI 助手扩展代码
- `chrome/android/` - Android 平台相关代码

### 构建要求
- Android SDK
- Chromium 构建工具链
- Node.js 和 pnpm（用于 Wiki 扩展开发）

### 最近更新
- ✅ 将浏览器名称从 Kiwi Browser 更改为 NanoWiki
- ✅ 将 nanobrowser 插件名称更改为 Wiki
- ✅ 优化 Wiki UI，采用 Anthropic/Claude 风格设计
- ✅ 移除所有推广内容（GitHub、Discord 等）
- ✅ 更新配色方案为温暖中性色调
- ✅ 完善深色模式支持

## 许可证

本项目基于 Apache 2.0 许可证开源。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进 NanoWiki 浏览器。

---

Copyright © 2025 NanoWiki.
