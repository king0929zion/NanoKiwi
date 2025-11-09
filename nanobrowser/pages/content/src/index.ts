// Content script for Wiki AI overlay effects
console.log('Wiki content script loaded');

// Create overlay styles
const overlayStyles = `
  #wiki-ai-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 2147483647;
    transition: opacity 0.3s ease;
  }
  
  #wiki-ai-overlay.active {
    pointer-events: auto;
  }
  
  /* Glow effect on screen edges */
  #wiki-ai-overlay::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(217, 119, 6, 0.3) 20%, 
      rgba(217, 119, 6, 0.5) 50%, 
      rgba(217, 119, 6, 0.3) 80%, 
      transparent 100%);
    animation: wiki-glow-top 2s ease-in-out infinite;
  }
  
  #wiki-ai-overlay::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(217, 119, 6, 0.3) 20%, 
      rgba(217, 119, 6, 0.5) 50%, 
      rgba(217, 119, 6, 0.3) 80%, 
      transparent 100%);
    animation: wiki-glow-bottom 2s ease-in-out infinite;
  }
  
  .wiki-glow-left {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background: linear-gradient(180deg, 
      transparent 0%, 
      rgba(217, 119, 6, 0.3) 20%, 
      rgba(217, 119, 6, 0.5) 50%, 
      rgba(217, 119, 6, 0.3) 80%, 
      transparent 100%);
    animation: wiki-glow-left 2s ease-in-out infinite;
  }
  
  .wiki-glow-right {
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background: linear-gradient(180deg, 
      transparent 0%, 
      rgba(217, 119, 6, 0.3) 20%, 
      rgba(217, 119, 6, 0.5) 50%, 
      rgba(217, 119, 6, 0.3) 80%, 
      transparent 100%);
    animation: wiki-glow-right 2s ease-in-out infinite;
  }
  
  @keyframes wiki-glow-top {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 0.7; }
  }
  
  @keyframes wiki-glow-bottom {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 0.7; }
  }
  
  @keyframes wiki-glow-left {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 0.7; }
  }
  
  @keyframes wiki-glow-right {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 0.7; }
  }
  
  /* Pause button */
  #wiki-pause-button {
    position: fixed;
    bottom: 24px;
    left: 50%;
    transform: translateX(-50%);
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: rgba(217, 119, 6, 0.9);
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2147483648;
    transition: all 0.3s ease;
    opacity: 0;
    pointer-events: none;
  }
  
  #wiki-pause-button.visible {
    opacity: 1;
    pointer-events: auto;
  }
  
  #wiki-pause-button:hover {
    background: rgba(217, 119, 6, 1);
    transform: translateX(-50%) scale(1.1);
  }
  
  #wiki-pause-button:active {
    transform: translateX(-50%) scale(0.95);
  }
  
  #wiki-pause-button svg {
    width: 24px;
    height: 24px;
    fill: white;
  }
  
  /* Dark mode adjustments */
  @media (prefers-color-scheme: dark) {
    #wiki-pause-button {
      background: rgba(217, 119, 6, 0.85);
      border-color: rgba(255, 255, 255, 0.15);
    }
  }
  
  /* Mobile optimizations */
  @media (max-width: 768px) {
    #wiki-pause-button {
      width: 48px;
      height: 48px;
      bottom: 16px;
    }
    
    #wiki-pause-button svg {
      width: 20px;
      height: 20px;
    }
  }
`;

// Inject styles
const styleSheet = document.createElement('style');
styleSheet.textContent = overlayStyles;
document.head.appendChild(styleSheet);

// Create overlay element
function createOverlay() {
  const overlay = document.createElement('div');
  overlay.id = 'wiki-ai-overlay';
  
  const glowLeft = document.createElement('div');
  glowLeft.className = 'wiki-glow-left';
  
  const glowRight = document.createElement('div');
  glowRight.className = 'wiki-glow-right';
  
  overlay.appendChild(glowLeft);
  overlay.appendChild(glowRight);
  
  document.body.appendChild(overlay);
  return overlay;
}

// Create pause button
function createPauseButton() {
  const button = document.createElement('div');
  button.id = 'wiki-pause-button';
  button.innerHTML = `
    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
      <rect x="6" y="4" width="4" height="16" rx="1"/>
      <rect x="14" y="4" width="4" height="16" rx="1"/>
    </svg>
  `;
  
  button.addEventListener('click', async () => {
    // Get current tab ID
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    const tabId = tabs[0]?.id;
    
    if (tabId) {
      // Send pause message to background script
      chrome.runtime.sendMessage({
        type: 'pause_task',
        tabId: tabId,
      }).catch(err => console.error('Failed to send pause message:', err));
    }
  });
  
  document.body.appendChild(button);
  return button;
}

// Initialize overlay and button
let overlay: HTMLElement | null = null;
let pauseButton: HTMLElement | null = null;

function initOverlay() {
  if (!overlay) {
    overlay = createOverlay();
  }
  if (!pauseButton) {
    pauseButton = createPauseButton();
  }
}

// Listen for messages from background script
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'show_wiki_overlay') {
    initOverlay();
    if (overlay) {
      overlay.classList.add('active');
      // Prevent user interaction with page
      document.body.style.pointerEvents = 'none';
      document.body.style.userSelect = 'none';
    }
    if (pauseButton) {
      pauseButton.classList.add('visible');
    }
  } else if (message.type === 'hide_wiki_overlay') {
    if (overlay) {
      overlay.classList.remove('active');
      document.body.style.pointerEvents = '';
      document.body.style.userSelect = '';
    }
    if (pauseButton) {
      pauseButton.classList.remove('visible');
    }
  }
  
  sendResponse({ success: true });
  return true;
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
  if (overlay) {
    overlay.remove();
  }
  if (pauseButton) {
    pauseButton.remove();
  }
});
