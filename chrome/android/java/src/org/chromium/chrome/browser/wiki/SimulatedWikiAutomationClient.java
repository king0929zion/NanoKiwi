package org.chromium.chrome.browser.wiki;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.VisibleForTesting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Lightweight automation client that simulates task execution for UI development. */
class SimulatedWikiAutomationClient implements WikiAutomationClient {
    private static final long STEP_DELAY_MS = 1500L;
    private static final List<String> DEFAULT_STEPS = Arrays.asList(
            "Analyzing \"%s\" in the active context\u2026",
            "Composing a deterministic navigation plan\u2026",
            "Executing scripted page interactions\u2026",
            "Capturing a verifiable activity log\u2026");

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mTickRunnable = this::emitNextStep;

    private WikiAutomationClient.Observer mObserver;
    private String mActivePrompt;
    private List<String> mPendingSteps = DEFAULT_STEPS;
    private int mCurrentStepIndex;
    private boolean mRunning;
    private boolean mCompleted;

    @Override
    public void startAutomation(String prompt, Observer observer) {
        cancel();
        mObserver = observer;
        mActivePrompt = prompt;
        mCurrentStepIndex = 0;
        mCompleted = false;
        mRunning = true;
        scheduleNextStep();
    }

    @Override
    public void pause() {
        if (!mRunning) return;
        mRunning = false;
        mHandler.removeCallbacks(mTickRunnable);
    }

    @Override
    public void resume() {
        if (mRunning || mCompleted) return;
        mRunning = true;
        scheduleNextStep();
    }

    @Override
    public void cancel() {
        mRunning = false;
        mCompleted = false;
        mHandler.removeCallbacks(mTickRunnable);
        mObserver = null;
    }

    @Override
    public void destroy() {
        cancel();
    }

    private void scheduleNextStep() {
        mHandler.postDelayed(mTickRunnable, STEP_DELAY_MS);
    }

    private void emitNextStep() {
        if (!mRunning || mObserver == null) return;

        if (mCurrentStepIndex < mPendingSteps.size()) {
            String status =
                    String.format(Locale.getDefault(), mPendingSteps.get(mCurrentStepIndex), mActivePrompt);
            mObserver.onStatusUpdated(status);
            mCurrentStepIndex++;
            scheduleNextStep();
            return;
        }

        mObserver.onAgentMessage(String.format(
                Locale.getDefault(),
                "Wiki just finished \"%s\" and documented the outcome for review.",
                mActivePrompt));
        mObserver.onCompleted("Automation is complete. Feel free to launch another task.");
        mCompleted = true;
        mRunning = false;
    }

    @VisibleForTesting
    void setSteps(List<String> steps) {
        mPendingSteps = steps;
    }
}
