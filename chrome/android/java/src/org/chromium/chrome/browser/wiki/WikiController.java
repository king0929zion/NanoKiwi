package org.chromium.chrome.browser.wiki;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chromium.base.metrics.RecordUserAction;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ChromeActivity;

import java.util.ArrayList;
import java.util.List;

/** Coordinates the in-browser Wiki assistant experience. */
public class WikiController {
    /** Observer for toolbar/UI integrations. */
    public interface WikiStateObserver {
        void onWikiExecutionStateChanged(boolean isRunning);
    }

    private final ChromeActivity mActivity;
    private final ViewGroup mRootView;
    private final WikiPanelView mPanelView;
    private final WikiAiOverlayView mOverlayView;
    private final WikiAutomationClient mAutomationClient;
    private final WikiAutomationClient.Observer mAutomationObserver;
    private final List<WikiStateObserver> mObservers = new ArrayList<>();
    private final List<WikiMessage> mMessages = new ArrayList<>();

    private boolean mPanelVisible;
    private boolean mAiExecuting;
    private boolean mAutomationPaused;
    private String mActivePrompt;

    public WikiController(ChromeActivity activity) {
        mActivity = activity;
        mRootView = activity.findViewById(android.R.id.content);
        LayoutInflater inflater = LayoutInflater.from(activity);
        mOverlayView =
                (WikiAiOverlayView) inflater.inflate(R.layout.wiki_ai_overlay, mRootView, false);
        mPanelView = (WikiPanelView) inflater.inflate(R.layout.wiki_panel_view, mRootView, false);
        mAutomationClient = new SimulatedWikiAutomationClient();
        mAutomationObserver = new WikiAutomationClient.Observer() {
            @Override
            public void onStatusUpdated(String statusCopy) {
                handleAutomationStatus(statusCopy);
            }

            @Override
            public void onAgentMessage(String messageCopy) {
                appendMessage(WikiMessage.Role.WIKI, messageCopy);
            }

            @Override
            public void onCompleted(String finalSummary) {
                finishAutomation(WikiAutomationState.COMPLETED, finalSummary);
            }

            @Override
            public void onError(String errorCopy) {
                finishAutomation(WikiAutomationState.ERROR, errorCopy);
            }
        };

        mRootView.addView(mOverlayView);
        mRootView.addView(mPanelView);

        mOverlayView.setOnPauseListener(this::pauseAutomation);
        mPanelView.setPanelCallback(new WikiPanelView.PanelCallback() {
            @Override
            public void onDismissRequested() {
                hidePanel();
            }

            @Override
            public void onPromptSubmitted(String prompt) {
                handlePrompt(prompt);
            }

            @Override
            public void onResumeRequested() {
                resumeAutomation();
            }
        });

        mPanelView.bindMessages(mMessages);
        mPanelView.setAutomationState(
                WikiAutomationState.IDLE, activity.getString(R.string.wiki_panel_status_idle), false);
    }

    /** Toggles the visibility of the Wiki panel. */
    public void togglePanel() {
        if (mPanelVisible) {
            hidePanel();
        } else {
            showPanel();
        }
    }

    public void showPanel() {
        if (mPanelVisible) return;
        mPanelVisible = true;
        mPanelView.animateIn();
    }

    public void hidePanel() {
        if (!mPanelVisible) return;
        mPanelVisible = false;
        mPanelView.animateOut(null);
    }

    public boolean onBackPressed() {
        if (mPanelVisible) {
            hidePanel();
            return true;
        }
        return false;
    }

    public void destroy() {
        mAutomationClient.destroy();
        mRootView.removeView(mPanelView);
        mRootView.removeView(mOverlayView);
        mObservers.clear();
    }

    public void addObserver(WikiStateObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    private void notifyObservers() {
        for (WikiStateObserver observer : mObservers) {
            observer.onWikiExecutionStateChanged(mAiExecuting);
        }
    }

    private void handlePrompt(String prompt) {
        if (TextUtils.isEmpty(prompt)) return;
        appendMessage(WikiMessage.Role.USER, prompt);
        mPanelView.clearPromptField();
        beginAutomation(prompt);
    }

    private void beginAutomation(String prompt) {
        if (mAiExecuting) return;
        mActivePrompt = prompt;
        mAutomationPaused = false;
        mAutomationClient.startAutomation(prompt, mAutomationObserver);
        mAiExecuting = true;
        RecordUserAction.record("MobileWikiAutomationStarted");
        hidePanel();
        showOverlay();
        mPanelView.setAutomationState(WikiAutomationState.RUNNING,
                mActivity.getString(R.string.wiki_panel_status_running), false);
        notifyObservers();
    }

    private void handleAutomationStatus(String statusCopy) {
        mPanelView.setAutomationState(WikiAutomationState.RUNNING, statusCopy, false);
        appendMessage(WikiMessage.Role.SYSTEM, statusCopy);
    }

    private void finishAutomation(WikiAutomationState finalState, String summaryCopy) {
        hideOverlay();
        mAiExecuting = false;
        mAutomationPaused = false;
        if (!TextUtils.isEmpty(summaryCopy)) {
            appendMessage(finalState == WikiAutomationState.ERROR ? WikiMessage.Role.SYSTEM
                                                                  : WikiMessage.Role.WIKI,
                    summaryCopy);
        }
        String statusCopy;
        switch (finalState) {
            case COMPLETED:
                statusCopy = mActivity.getString(R.string.wiki_panel_status_completed);
                break;
            case ERROR:
                statusCopy = mActivity.getString(R.string.wiki_panel_status_error);
                break;
            default:
                statusCopy = mActivity.getString(R.string.wiki_panel_status_idle);
                break;
        }
        mPanelView.setAutomationState(finalState, statusCopy, false);
        notifyObservers();
    }

    private void pauseAutomation() {
        if (!mAiExecuting) return;
        mAutomationClient.pause();
        mAiExecuting = false;
        mAutomationPaused = true;
        hideOverlay();
        mPanelView.setAutomationState(WikiAutomationState.PAUSED,
                mActivity.getString(R.string.wiki_panel_status_paused), true);
        showPanel();
        notifyObservers();
    }

    private void resumeAutomation() {
        if (!mAutomationPaused) return;
        mAutomationClient.resume();
        mAutomationPaused = false;
        mAiExecuting = true;
        hidePanel();
        showOverlay();
        mPanelView.setAutomationState(WikiAutomationState.RUNNING,
                mActivity.getString(R.string.wiki_panel_status_running), false);
        notifyObservers();
    }

    private void showOverlay() {
        mOverlayView.setVisibility(View.VISIBLE);
        mOverlayView.setAlpha(0f);
        mOverlayView.animate().alpha(1f).setDuration(220L).start();
    }

    private void hideOverlay() {
        if (mOverlayView.getVisibility() != View.VISIBLE) return;
        mOverlayView.animate()
                .alpha(0f)
                .setDuration(180L)
                .withEndAction(() -> {
                    mOverlayView.setVisibility(View.GONE);
                    mOverlayView.setAlpha(1f);
                })
                .start();
    }

    private void appendMessage(WikiMessage.Role role, String text) {
        if (TextUtils.isEmpty(text)) return;
        mMessages.add(new WikiMessage(role, text, System.currentTimeMillis()));
        mPanelView.bindMessages(mMessages);
    }
}
