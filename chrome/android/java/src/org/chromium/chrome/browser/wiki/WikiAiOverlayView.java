// Copyright 2025 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.wiki;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import org.chromium.chrome.R;

/** Full-screen overlay that blocks user interaction while the AI agent is executing. */
public class WikiAiOverlayView extends FrameLayout {
    private ImageButton mPauseButton;
    private Runnable mPauseListener;

    public WikiAiOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPauseButton = findViewById(R.id/wiki_pause_button);
        mPauseButton.setOnClickListener(v -> {
            if (mPauseListener != null) {
                mPauseListener.run();
            }
        });
    }

    public void setOnPauseListener(Runnable listener) {
        mPauseListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Consume touches so they don’t reach the web content.
        return true;
    }
}
