package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.connect;

import dev.mayuna.cinnamonroll.extension.frames.loading.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.ClientConstants;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.util.DesktopUtils;

import java.awt.event.MouseEvent;
import java.util.concurrent.CompletableFuture;

/**
 * Connect frame, is the first frame that the user sees when they open the client<br>
 */
public final class ConnectFrame extends ConnectFrameDesign {

    public ConnectFrame() {
        super();

        // Focus the connect button if the last server address is valid
        if (GraphicalUserInterface.INSTANCE.getSettings().isLastServerAddressValid()) {
            buttonConnect.requestFocus();
        }
    }

    @Override
    protected void prepareComponents() {
        super.prepareComponents();

        labelVersion.setText($formatTranslation(Lang.Frames.Connect.LABEL_VERSION, "2.0", "N/A"));

        // Set the server address to the last used server address
        fieldServerAddress.setText(GraphicalUserInterface.INSTANCE.getSettings().getLastServerAddress());
    }

    @Override
    protected void clickConnect(MouseEvent mouseEvent) {
        // Set the last server address to the config
        GraphicalUserInterface.INSTANCE.getSettings().setLastServerAddress(fieldServerAddress.getText());
        GraphicalUserInterface.INSTANCE.getSettings().save(); // Save

        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.Connect.TEXT_CONNECTING));
        loadingDialog.blockAndShow(this);

        if (!SakuyaBridge.INSTANCE.prepareForConnect()) {
            loadingDialog.unblockAndClose();
            TranslatedInfoMessage.create($getTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_PREPARE_CLIENT)).showError(this);
            return;

        }

        CompletableFuture.runAsync(() -> {
            // Connect
            var connectResult = SakuyaBridge.INSTANCE.connectToServer(fieldServerAddress.getText()).join();

            if (!connectResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_CONNECT, connectResult.getErrorMessage()))
                                     .showError(this);
                return;
            }

            // Exchange versions
            loadingDialog.appendProgressText($getTranslation(Lang.Frames.Connect.TEXT_CHECKING_VERSION));

            var exchangeVersionsResult = SakuyaBridge.INSTANCE.exchangeVersions().join();

            if (!exchangeVersionsResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_EXCHANGE_VERSIONS, exchangeVersionsResult.getErrorMessage()))
                                     .showError(this);
                return;
            }

            // Fetch server info
            loadingDialog.appendProgressText($getTranslation(Lang.Frames.Connect.TEXT_FETCHING_SERVER_INFO));

            var serverInfoResult = SakuyaBridge.INSTANCE.fetchServerInfo().join();

            if (!serverInfoResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_FETCH_SERVER_INFO, serverInfoResult.getErrorMessage()))
                                     .showError(this);
                return;
            }

            loadingDialog.unblockAndClose();

            // Close this frame
            this.dispose();

            // Open the Server Info frame
            GraphicalUserInterface.INSTANCE.openServerInfo(serverInfoResult.getResult());
        });
    }

    @Override
    protected void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    @Override
    protected void clickAuthor(MouseEvent mouseEvent) {
        DesktopUtils.openUrl(ClientConstants.MAYUNA_WEBSITE_URL);
    }

    @Override
    protected void clickVersion(MouseEvent mouseEvent) {
        DesktopUtils.openUrl(ClientConstants.GITHUB_RELEASE_URL);
    }
}
