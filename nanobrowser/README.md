# Wiki Assistant

Wiki is the native AI assistant that ships with the NanoWiki browser and can also run as a standalone Chrome extension. It mirrors the calm Anthropics aestheticsoft neutrals, generous radius, typography-firstand keeps multi-agent automation within arms reach on both desktop and mobile.

## Principles
- **Claude-inspired UI**  warm palette, zero gradients, unified corner radius.
- **No promos**  the surface is entirely task-focused (no Discord/GitHub badges or sponsorship CTAs).
- **English + Simplified Chinese**  matches the language scope of NanoWiki across UI, docs, and i18n payloads.
- **Native orchestration**  shares the same concepts as the Android Wiki panel (conversation feed, status chip, pause overlay, replay).

## Getting Wiki
### Preinstalled with NanoWiki
1. Click the **Wiki** icon in the NanoWiki toolbar to open the side panel.
2. Follow the onboarding card to add your available API keys (Anthropic, OpenAI, Gemini, Ollama, etc.).
3. Describe a task and let Wiki drive the browser. During automation the Android build renders the same glow + pause affordance as the desktop panel.

### Standalone development build
`ash
git clone https://github.com/nanowiki/wiki.git
cd wiki/nanobrowser
pnpm install
pnpm dev
`
1. Visit chrome://extensions, enable Developer Mode, and load 
anobrowser/dist.
2. Open the Wiki icon in the Chrome extensions tray to launch the panel.

## Quick start
1. Use the gear icon → **General → Models** to store API keys.
2. Save frequent instructions under **Wiki Templates** (e.g., Summarize this page or List every price block).
3. While the agent is running, tap the floating pause button or reopen the panel to inspect progress and replay steps.

## Contributing
- UI lives under pages/side-panel/src/ (SidePanel.tsx + SidePanel.css).
- Localized strings reside in packages/i18n/locales/{en,zh_CN}/messages.json.
- Storage, bookmarks, and history helpers are inside packages/storage.
- Please keep contributions aligned with the Anthropics palette and refrain from reintroducing marketing widgets.

## Privacy & License
- Privacy Policy: PRIVACY.md
- License: Apache-2.0

9 2025 NanoWiki.
