package org.chromium.chrome.browser.wiki;

/** Abstraction responsible for executing automation tasks on behalf of the Wiki assistant. */
interface WikiAutomationClient {
    /** Receives progress updates from an active automation run. */
    interface Observer {
        void onStatusUpdated(String statusCopy);

        void onAgentMessage(String messageCopy);

        void onCompleted(String finalSummary);

        void onError(String errorCopy);
    }

    /** Starts a new automation run. */
    void startAutomation(String prompt, Observer observer);

    /** Pauses the currently running automation, if supported. */
    void pause();

    /** Resumes a paused automation session. */
    void resume();

    /** Cancels any pending work. */
    void cancel();

    /** Releases resources when the controller is destroyed. */
    void destroy();
}
