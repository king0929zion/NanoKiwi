package org.chromium.chrome.browser.wiki;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.List;

/** RecyclerView adapter that renders the Wiki conversation transcript. */
class WikiMessageAdapter extends RecyclerView.Adapter<WikiMessageAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_WIKI = 1;
    private static final int VIEW_TYPE_SYSTEM = 2;

    private final LayoutInflater mInflater;
    private final List<WikiMessage> mMessages = new ArrayList<>();

    WikiMessageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    void setMessages(List<WikiMessage> messages) {
        mMessages.clear();
        if (messages != null) {
            mMessages.addAll(messages);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.wiki_message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        WikiMessage message = mMessages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        WikiMessage.Role role = mMessages.get(position).getRole();
        if (role == WikiMessage.Role.USER) return VIEW_TYPE_USER;
        if (role == WikiMessage.Role.SYSTEM) return VIEW_TYPE_SYSTEM;
        return VIEW_TYPE_WIKI;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView mMessageView;

        MessageViewHolder(View itemView) {
            super(itemView);
            mMessageView = itemView.findViewById(R.id.wiki_message_text);
        }

        void bind(WikiMessage message) {
            mMessageView.setText(message.getText());
            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams) mMessageView.getLayoutParams();
            params.setMarginStart(dp(8));
            params.setMarginEnd(dp(8));

            switch (message.getRole()) {
                case USER:
                    params.gravity = Gravity.END;
                    mMessageView.setBackgroundResource(R.drawable.wiki_message_user_background);
                    mMessageView.setTextColor(
                            ContextCompat.getColor(mMessageView.getContext(),
                                    R.color.wiki_on_accent_text));
                    break;
                case SYSTEM:
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    mMessageView.setBackgroundResource(R.drawable.wiki_message_ai_background);
                    mMessageView.setTextColor(
                            ContextCompat.getColor(mMessageView.getContext(),
                                    R.color.wiki_text_secondary));
                    break;
                case WIKI:
                default:
                    params.gravity = Gravity.START;
                    mMessageView.setBackgroundResource(R.drawable.wiki_message_ai_background);
                    mMessageView.setTextColor(
                            ContextCompat.getColor(mMessageView.getContext(),
                                    R.color.wiki_text_primary));
                    break;
            }
            mMessageView.setLayoutParams(params);
        }

        private int dp(int value) {
            float density = mMessageView.getResources().getDisplayMetrics().density;
            return Math.round(value * density);
        }
    }
}
