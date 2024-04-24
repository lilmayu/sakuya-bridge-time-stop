package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.connect;

import dev.mayuna.cinnamonroll.extension.frames.loading.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.MiscConstants;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.DesktopUtils;

import java.awt.event.MouseEvent;
import java.util.concurrent.CompletableFuture;

/**
 * Connect frame, is the first frame that the user sees when they open the client<br>
 */
public final class ConnectFrame extends ConnectFrameDesign {

    @Override
    protected void prepareComponents() {
        super.prepareComponents();

        labelVersion.setText($formatTranslation(Lang.Frames.Connect.LABEL_VERSION, "2.0", "N/A"));
    }

    @Override
    protected void clickConnect(MouseEvent mouseEvent) {
        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.Connect.TEXT_CONNECTING));
        loadingDialog.blockAndShow(this);

        SakuyaBridge.INSTANCE.prepareForConnect();

        CompletableFuture.runAsync(() -> {
            // Connect
            var connectResult = SakuyaBridge.INSTANCE.connectToServer(fieldServerAddress.getText()).join();

            if (!connectResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_CONNECT, connectResult.getErrorMessage())).showError(this);
                return;
            }

            // Exchange versions
            loadingDialog.appendProgressText($getTranslation(Lang.Frames.Connect.TEXT_CHECKING_VERSION));

            var exchangeVersionsResult = SakuyaBridge.INSTANCE.exchangeVersions().join();

            if (!exchangeVersionsResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_EXCHANGE_VERSIONS, exchangeVersionsResult.getErrorMessage())).showError(this);
                return;
            }

            // Fetch server info
            loadingDialog.appendProgressText($getTranslation(Lang.Frames.Connect.TEXT_FETCHING_SERVER_INFO));

            var serverInfoResult = SakuyaBridge.INSTANCE.fetchServerInfo().join();

            if (!serverInfoResult.isSuccessful()) {
                loadingDialog.unblockAndClose();
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Connect.TEXT_FAILED_TO_FETCH_SERVER_INFO, serverInfoResult.getErrorMessage())).showError(this);
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
        DesktopUtils.openUrl(MiscConstants.MAYUNA_WEBSITE_URL);
    }

    @Override
    protected void clickVersion(MouseEvent mouseEvent) {
        DesktopUtils.openUrl(MiscConstants.GITHUB_RELEASE_URL);
    }
}
