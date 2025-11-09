package org.chromium.chrome.browser.wiki;

/** Immutable conversation entry rendered inside the Wiki panel. */
class WikiMessage {
    /** Author of a message. */
    enum Role {
        USER,
        WIKI,
        SYSTEM
    }

    private final Role mRole;
    private final String mText;
    private final long mTimestamp;

    WikiMessage(Role role, String text, long timestamp) {
        mRole = role;
        mText = text;
        mTimestamp = timestamp;
    }

    Role getRole() {
        return mRole;
    }

    String getText() {
        return mText;
    }

    long getTimestamp() {
        return mTimestamp;
    }
}
