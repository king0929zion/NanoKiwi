package org.chromium.chrome.browser.wiki;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;

import java.util.Collections;
import java.util.List;

/** Container view hosting the Wiki panel card. */
public class WikiPanelView extends FrameLayout {
    /** Listener for panel interactions. */
    interface PanelCallback {
        void onDismissRequested();

        void onPromptSubmitted(String prompt);

        void onResumeRequested();
    }

    private LinearLayout mCard;
    private ImageButton mCloseButton;
    private ImageButton mSendButton;
    private Button mResumeButton;
    private TextView mStatusChip;
    private TextView mEmptyState;
    private AppCompatEditText mPromptInput;
    private RecyclerView mMessageList;
    private WikiMessageAdapter mAdapter;
    private PanelCallback mCallback;
    private WikiAutomationState mCurrentState = WikiAutomationState.IDLE;

    public WikiPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCard = findViewById(R.id/wiki_panel_card);
        mCloseButton = findViewById(R.id.wiki_close_button);
        mSendButton = findViewById(R.id.wiki_send_button);
        mResumeButton = findViewById(R.id.wiki_resume_button);
        mStatusChip = findViewById(R.id.wiki_status_chip);
        mEmptyState = findViewById(R.id.wiki_empty_state);
        mPromptInput = findViewById(R.id.wiki_prompt_input);
        mMessageList = findViewById(R.id.wiki_message_list);

        mAdapter = new WikiMessageAdapter(getContext());
        mMessageList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageList.setAdapter(mAdapter);

        mCloseButton.setOnClickListener(v -> {
            if (mCallback != null) mCallback.onDismissRequested();
        });
        mSendButton.setOnClickListener(v -> emitPromptIfNeeded());
        mResumeButton.setOnClickListener(v -> {
            if (mCallback != null) mCallback.onResumeRequested();
        });

        mPromptInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                emitPromptIfNeeded();
                return true;
            }
            return false;
        });
    }

    void setPanelCallback(PanelCallback callback) {
        mCallback = callback;
    }

    void bindMessages(List<WikiMessage> messages) {
        List<WikiMessage> safeList = messages != null ? messages : Collections.emptyList();
        mAdapter.setMessages(safeList);
        boolean hasMessages = !safeList.isEmpty();
        mEmptyState.setVisibility(hasMessages ? GONE : VISIBLE);
        if (hasMessages) {
            mMessageList.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    void setAutomationState(WikiAutomationState state, String statusText, boolean showResume) {
        mCurrentState = state;
        mStatusChip.setText(statusText);
        if (mStatusChip.getBackground() != null) {
            Drawable tinted = DrawableCompat.wrap(mStatusChip.getBackground().mutate());
            DrawableCompat.setTint(tinted,
                    ContextCompat.getColor(getContext(), state.getChipColorRes()));
            mStatusChip.setBackground(tinted);
        }
        mResumeButton.setVisibility(showResume ? VISIBLE : GONE);
        setInputEnabled(state == WikiAutomationState.IDLE || state == WikiAutomationState.COMPLETED
                || state == WikiAutomationState.ERROR);
    }

    void setInputEnabled(boolean enabled) {
        mPromptInput.setEnabled(enabled);
        mSendButton.setEnabled(enabled);
    }

    void clearPromptField() {
        mPromptInput.setText("");
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            hideKeyboard();
        }
    }

    void animateIn() {
        setVisibility(View.VISIBLE);
        setAlpha(0f);
        float offset = mCard != null && mCard.getHeight() > 0 ? mCard.getHeight() * 0.1f : 40f;
        setTranslationY(offset);
        animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(220L)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                .start();
    }

    void animateOut(Runnable endAction) {
        float offset = mCard != null && mCard.getHeight() > 0 ? mCard.getHeight() * 0.08f : 32f;
        animate()
                .alpha(0f)
                .translationY(offset)
                .setDuration(160L)
                .withEndAction(() -> {
                    setVisibility(View.GONE);
                    setAlpha(1f);
                    setTranslationY(0f);
                    if (endAction != null) endAction.run();
                })
                .start();
    }

    WikiAutomationState getCurrentState() {
        return mCurrentState;
    }

    private void emitPromptIfNeeded() {
        if (mCallback == null) return;
        CharSequence text = mPromptInput.getText();
        String prompt = text != null ? text.toString().trim() : "";
        if (TextUtils.isEmpty(prompt)) return;
        mCallback.onPromptSubmitted(prompt);
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && mPromptInput != null) {
            imm.hideSoftInputFromWindow(mPromptInput.getWindowToken(), 0);
        }
    }
}
