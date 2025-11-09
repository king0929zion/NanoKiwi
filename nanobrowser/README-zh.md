# Wiki 助手

Wiki 是 NanoWiki 浏览器内置的原生 AI 侧边栏，也可以作为独立 Chrome 扩展运行。它传承 Claude / Anthropic 的温润配色和圆角细节，在桌面与移动端提供一致的多代理体验，帮助你驱动浏览器执行长流程任务、采集数据与整理研究。

## 设计原则
- **Anthropic 风格**：暖色中性色 + 统一圆角，全局无渐变、无高饱和色。
- **零推广**：界面内不再出现 Discord、GitHub 徽章或赞助文案，只专注于任务本身。
- **仅英文与简中**：UI、文档、i18n 资源都与 NanoWiki 的语言策略保持一致。
- **原生联动**：与 Android 端 Wiki 面板共用会话流、状态芯片、暂停/回放控件。

## 获取方式
### NanoWiki 随附版本
1. 在 NanoWiki 工具栏点击 **Wiki** 图标即可打开侧边栏。
2. 按照步骤在设置页填写可用的 API key（Anthropic、OpenAI、Gemini、Ollama 等）。
3. 输入任务描述即可让 Wiki 自动驾驶浏览器，Android 端会同步呈现柔和光效与暂停按钮。

### 独立开发构建
`ash
git clone https://github.com/nanowiki/wiki.git
cd wiki/nanobrowser
pnpm install
pnpm dev
`
1. 进入 chrome://extensions，启用开发者模式后加载 
anobrowser/dist。
2. 在扩展托盘中点击 Wiki 图标打开侧边栏。

## 快速上手
1. 通过齿轮按钮 → **General → Models** 添加 API key。
2. 在 “Wiki Templates” 中保存常用指令，例如“总结当前页面”或“列出所有价格段”。
3. 任务执行期间可随时点击底部悬浮的暂停按钮，或重新展开面板查看状态与回放步骤。

## 开发提示
- UI 主体位于 pages/side-panel/src/（SidePanel.tsx 与 SidePanel.css）。
- 多语言字符串位于 packages/i18n/locales/{en,zh_CN}/messages.json。
- 存储、历史与书签逻辑在 packages/storage 中实现。
- 欢迎通过 Issues/PR 贡献改进，但请保持配色风格，并避免重新加入推广内容。

## 隐私与许可证
- 隐私政策：PRIVACY.md
- 许可证：Apache-2.0

版权所有 © 2025 NanoWiki。
