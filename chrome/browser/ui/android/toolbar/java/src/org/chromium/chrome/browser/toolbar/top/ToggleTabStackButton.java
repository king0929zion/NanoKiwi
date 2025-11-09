// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.toolbar.top;

import android.content.Context;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;

import org.chromium.base.TraceEvent;
import org.chromium.chrome.browser.theme.ThemeUtils;
import org.chromium.chrome.browser.toolbar.R;
import org.chromium.chrome.browser.toolbar.TabCountProvider;
import org.chromium.chrome.browser.toolbar.TabSwitcherDrawable;
import org.chromium.chrome.browser.ui.theme.BrandedColorScheme;
import org.chromium.components.browser_ui.widget.listmenu.ListMenuButton;
import org.chromium.ui.widget.Toast;

/**
 * A button displaying the number of open tabs. Clicking the button toggles the tab switcher view.
 * TODO(twellington): Replace with TabSwitcherButtonCoordinator so code can be shared with bottom
 *                    toolbar.
 */
public class ToggleTabStackButton
        extends ListMenuButton implements TabCountProvider.TabCountObserver, View.OnClickListener,
                                          View.OnLongClickListener {
    private TabSwitcherDrawable mTabSwitcherButtonDrawable;
    private TabCountProvider mTabCountProvider;
    private OnClickListener mTabSwitcherListener;
    private OnLongClickListener mTabSwitcherLongClickListener;
    private boolean mWikiAppearanceEnabled;
    private boolean mWikiGlowEnabled;
    private int mCurrentTint;

    public ToggleTabStackButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mTabSwitcherButtonDrawable = TabSwitcherDrawable.createTabSwitcherDrawable(
                getContext(), BrandedColorScheme.APP_DEFAULT);
        setImageDrawable(mTabSwitcherButtonDrawable);
        mCurrentTint =
                ThemeUtils.getThemedToolbarIconTint(getContext(), BrandedColorScheme.APP_DEFAULT);
        enableWikiAppearance();
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * Called to destroy the tab stack button.
     */
    void destroy() {
        if (mTabCountProvider != null) mTabCountProvider.removeObserver(this);
    }

    void enableWikiAppearance() {
        if (mWikiAppearanceEnabled) return;
        mWikiAppearanceEnabled = true;
        setImageResource(R.drawable.wiki_ai_icon);
        setContentDescription(getResources().getString(R.string.accessibility_wiki_button));
        ImageViewCompat.setImageTintList(
                this, ColorStateList.valueOf(resolveWikiTint(/*glow=*/false)));
    }

    /**
     * Sets the OnClickListener that will be notified when the TabSwitcher button is pressed.
     * @param listener The callback that will be notified when the TabSwitcher button is pressed.
     */
    void setOnTabSwitcherClickHandler(OnClickListener listener) {
        mTabSwitcherListener = listener;
    }

    /**
     * Sets the OnLongClickListern that will be notified when the TabSwitcher button is long
     *         pressed.
     * @param listener The callback that will be notified when the TabSwitcher button is long
     *         pressed.
     */
    void setOnTabSwitcherLongClickHandler(OnLongClickListener listener) {
        mTabSwitcherLongClickListener = listener;
    }

    void setBrandedColorScheme(@BrandedColorScheme int brandedColorScheme) {
        mCurrentTint = ThemeUtils.getThemedToolbarIconTint(getContext(), brandedColorScheme);
        if (mWikiAppearanceEnabled) {
            ImageViewCompat.setImageTintList(
                    this, ColorStateList.valueOf(resolveWikiTint(mWikiGlowEnabled)));
        } else {
            mTabSwitcherButtonDrawable.setTint(mCurrentTint);
        }
    }

    /**
     * @param provider The {@link TabCountProvider} used to observe the number of tabs in the
     *                 current model.
     */
    void setTabCountProvider(TabCountProvider provider) {
        if (mWikiAppearanceEnabled) return;
        mTabCountProvider = provider;
        mTabCountProvider.addObserverAndTrigger(this);
    }

    @Override
    public void onTabCountChanged(int numberOfTabs, boolean isIncognito) {
        if (mWikiAppearanceEnabled) return;
        setEnabled(numberOfTabs >= 1);
        mTabSwitcherButtonDrawable.updateForTabCount(numberOfTabs, isIncognito);
    }

    @Override
    public void onClick(View v) {
        if (mTabSwitcherListener != null && isClickable()) {
            mTabSwitcherListener.onClick(this);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mTabSwitcherLongClickListener != null && isLongClickable()) {
            return mTabSwitcherLongClickListener.onLongClick(v);
        } else {
            CharSequence description = mWikiAppearanceEnabled
                    ? getResources().getString(R.string.accessibility_wiki_button)
                    : getResources().getString(R.string.open_tabs);
            return Toast.showAnchoredToast(getContext(), v, description);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try (TraceEvent e = TraceEvent.scoped("ToggleTabStackButton.onMeasure")) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try (TraceEvent e = TraceEvent.scoped("ToggleTabStackButton.onLayout")) {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    void setWikiGlow(boolean glow) {
        if (!mWikiAppearanceEnabled) return;
        if (mWikiGlowEnabled == glow) return;
        mWikiGlowEnabled = glow;
        float scale = glow ? 1.08f : 1f;
        animate().scaleX(scale).scaleY(scale).setDuration(180L).start();
        ImageViewCompat.setImageTintList(
                this, ColorStateList.valueOf(resolveWikiTint(glow)));
    }

    private int resolveWikiTint(boolean glowActive) {
        if (glowActive) {
            return getResources().getColor(R.color.wiki_glow_secondary);
        }
        return mCurrentTint != 0 ? mCurrentTint
                                 : ThemeUtils.getThemedToolbarIconTint(
                                           getContext(), BrandedColorScheme.APP_DEFAULT);
    }
}
