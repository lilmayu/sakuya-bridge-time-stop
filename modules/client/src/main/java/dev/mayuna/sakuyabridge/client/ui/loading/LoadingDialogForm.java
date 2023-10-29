package dev.mayuna.sakuyabridge.client.ui.loading;

import java.awt.*;

public class LoadingDialogForm extends LoadingDialogFormDesign {

    private Component parentComponent;
    private String progressInfoText;

    public LoadingDialogForm(String progressInfoText) {
        super(null);
        setProgressInfo(progressInfoText);
    }

    // Static methods for specific loading dialogs

    public static LoadingDialogForm createConnecting() {
        return new LoadingDialogForm("Connecting...");
    }

    public static LoadingDialogForm createFetchingLoginMethods() {
        return new LoadingDialogForm("Fetching login methods...");
    }

    // ===========================================

    @Override
    protected void loadData() {
    }

    /**
     * Block (disable) parent window and show this dialog
     *
     * @param parentComponent Parent window
     *
     * @return This dialog
     */
    public LoadingDialogForm blockAndShow(Component parentComponent) {
        this.parentComponent = parentComponent;

        if (parentComponent != null) {
            parentComponent.setEnabled(false);
        }

        this.setLocationRelativeTo(parentComponent);
        this.setVisible(true);

        return this;
    }

    /**
     * Unblock (enable) parent window and closes this dialog
     */
    public void unblockAndClose() {
        if (parentComponent != null) {
            parentComponent.setEnabled(true);
        }

        this.dispose();
    }

    /**
     * Sets the progress bar's maximum
     *
     * @param maxProgress The maximum progress
     */
    public void setMaxProgress(int maxProgress) {
        progressBar.setMaximum(maxProgress);
    }

    /**
     * Sets the progress bar's progress<br>This also disables indeterminate mode
     *
     * @param progress    The progress
     * @param maxProgress The maximum progress
     */
    public void setProgress(int progress, int maxProgress) {
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setMaximum(maxProgress);
        progressBar.setValue(progress);
    }

    /**
     * Sets the progress bar's progress<br>This also disables indeterminate mode
     *
     * @param progress The progress
     */
    public void setProgress(int progress) {
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setValue(progress);
    }

    /**
     * Sets text under the progress bar
     *
     * @param progressInfo The text
     */
    public void setProgressInfo(String progressInfo) {
        this.progressInfoText = progressInfo;
        this.progressInfo.setText(progressInfo);
    }

    /**
     * Appends text under the progress bar
     *
     * @param progressInfo The text
     */
    public void appendProgressInfo(String progressInfo) {
        if (progressInfo == null) {
            this.progressInfo.setText(this.progressInfoText);
            return;
        }

        this.progressInfo.setText(this.progressInfoText + " " + progressInfo);
    }

    /**
     * Sets the progress bar to indeterminate mode
     */
    public void setIndeterminate() {
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
    }
}
