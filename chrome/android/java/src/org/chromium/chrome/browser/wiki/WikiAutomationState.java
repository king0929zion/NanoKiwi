package org.chromium.chrome.browser.wiki;

import androidx.annotation.ColorRes;

import org.chromium.chrome.R;

/** Describes the current automation lifecycle for the Wiki assistant. */
public enum WikiAutomationState {
    IDLE(R.color.wiki_chip_surface),
    RUNNING(R.color.wiki_chip_running),
    PAUSED(R.color.wiki_chip_paused),
    COMPLETED(R.color.wiki_chip_completed),
    ERROR(R.color.wiki_chip_error);

    @ColorRes
    private final int mChipColorRes;

    WikiAutomationState(@ColorRes int chipColorRes) {
        mChipColorRes = chipColorRes;
    }

    @ColorRes
    int getChipColorRes() {
        return mChipColorRes;
    }
}
