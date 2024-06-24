package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.sakuyabridge.client.v1.ui.loading.LoadingDialogForm;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import lombok.Getter;

import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Panel for creating a game
 */
@Getter
public final class GameCreatorPanel extends GameCreatorPanelDesign {

    private final Consumer<Packets.Responses.Game.CreateGame> onGameCreated;
    private GameInfo gameInfo;
    private String gamePassword = null;

    /**
     * Creates a new game creator panel
     *
     * @param gameInfo      Nullable game info (if null, loads from config or creates new)
     * @param onGameCreated Invoked upon successful game creation
     */
    public GameCreatorPanel(GameInfo gameInfo, Consumer<Packets.Responses.Game.CreateGame> onGameCreated) {
        super();
        this.gameInfo = gameInfo;
        this.onGameCreated = onGameCreated;

        onOpen();
    }

    /**
     * Creates a new game creator panel
     *
     * @param onGameCreated Invoked upon successful game creation
     *
     * @see #GameCreatorPanel(GameInfo, Consumer)
     */
    public GameCreatorPanel(Consumer<Packets.Responses.Game.CreateGame> onGameCreated) {
        this(null, onGameCreated);
    }

    /**
     * Checks if the game name is valid
     *
     * @return True if valid
     */
    @Override
    protected boolean isGameNameValid() {
        String gameName = textFieldGameName.getText().trim();

        if (gameName.isBlank()) {
            return false;
        }

        return gameName.length() > CommonConstants.MIN_GAME_NAME_LENGTH && gameName.length() <= CommonConstants.MAX_GAME_NAME_LENGTH;
    }

    /**
     * Checks if the password is valid
     *
     * @return True if valid
     */
    @Override
    protected boolean isPasswordValid() {
        if (!checkBoxGamePasswordProtected.isSelected()) {
            return true;
        }

        String password = new String(textFieldGamePassword.getPassword());

        if (password.isBlank()) {
            return false;
        }

        return password.length() <= CommonConstants.MAX_PASSWORD_LENGTH;
    }

    /**
     * Processes the click on the create game button
     *
     * @param mouseEvent The mouse event
     */
    @Override
    protected void clickCreateGame(MouseEvent mouseEvent) {
        gameInfo.setName(textFieldGameName.getText().trim());
        gameInfo.setRegion((GameInfo.Region) comboBoxRegion.getSelectedItem());
        gameInfo.setVersion((GameInfo.Version) comboBoxGameVersion.getSelectedItem());
        gameInfo.setTechnology((GameInfo.Technology) comboBoxGameTechnology.getSelectedItem());
        gameInfo.setPreferredPlayerSide((GameInfo.PlayerSide) comboBoxGamePlayerSide.getSelectedItem());
        gameInfo.setPrivateGame(checkBoxGamePrivate.isSelected());
        gameInfo.setPasswordProtected(checkBoxGamePasswordProtected.isSelected());
        gameInfo.setDynamicPingStrategyEnabled(checkBoxDynamicPingStrategy.isSelected());
        gameInfo.setUdpHolePunchingEnabled(checkBoxUdpHolePunching.isSelected());

        var passwordArray = textFieldGamePassword.getPassword();

        if (passwordArray.length > 0) {
            gamePassword = new String(passwordArray);
        }

        SakuyaBridge.INSTANCE.getConfig().setLastGameInfo(gameInfo);
        SakuyaBridge.INSTANCE.getConfig().save();

        var loadingDialog = LoadingDialogForm.createCreatingGame().blockAndShow(GraphicalUserInterface.INSTANCE.getMainFrame());
        SakuyaBridge.INSTANCE.createGame(gameInfo, gamePassword).thenAcceptAsync(result -> {
            loadingDialog.unblockAndClose();

            if (!result.isSuccessful()) {
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.Main.Panels.HostGame.TEXT_FAILED_TO_CREATE_GAME, result.getErrorMessage()))
                                     .showError(GraphicalUserInterface.INSTANCE.getMainFrame());
                return;
            }

            onGameCreated.accept(result.getResult());
        });
    }

    /**
     * Checks if the game is password protected
     *
     * @param event The event
     */
    @Override
    protected void checkGamePasswordProtected(ActionEvent event) {
        boolean passwordProtected = checkBoxGamePasswordProtected.isSelected();

        textFieldGamePassword.setEnabled(passwordProtected);

        if (!passwordProtected) {
            textFieldGamePassword.setText("");
            CinnamonRollFlatLaf.setOutlinePropertyError(textFieldGamePassword, false);
        } else {
            if (textFieldGamePassword.getPassword().length == 0) {
                CinnamonRollFlatLaf.setOutlinePropertyError(textFieldGamePassword, true);
            }
        }

        updateComponents();
    }

    /**
     * Invoked when some component's action listener fired
     *
     * @param event The event
     */
    @Override
    protected void updateComponentSettings(ActionEvent event) {
        updateComponents();
    }

    /**
     * Invoked when some document listener fired
     *
     * @param documentEvent The event
     */
    @Override
    protected void updateDocumentSettings(DocumentEvent documentEvent) {
        updateComponents();
    }

    /**
     * Loads data from the game info into the components
     */
    @Override
    protected void loadData() {
        textFieldGameName.setText(gameInfo.getName());
        comboBoxRegion.setSelectedItem(gameInfo.getRegion());
        comboBoxGameVersion.setSelectedItem(gameInfo.getVersion());
        comboBoxGameTechnology.setSelectedItem(gameInfo.getTechnology());
        comboBoxGamePlayerSide.setSelectedItem(gameInfo.getPreferredPlayerSide());
        checkBoxGamePrivate.setSelected(gameInfo.isPrivateGame());
        checkBoxGamePasswordProtected.setSelected(gameInfo.isPasswordProtected());
        textFieldGamePassword.setText(gamePassword);
        checkBoxDynamicPingStrategy.setSelected(gameInfo.isDynamicPingStrategyEnabled());
        checkBoxUdpHolePunching.setSelected(gameInfo.isUdpHolePunchingEnabled());

        checkGamePasswordProtected(null);
    }

    /**
     * Updates the components based on the current state
     */
    private void updateComponents() {
        buttonCreateGame.setEnabled(isEverythingSelected());
    }

    /**
     * Invoked when the panel is opened
     */
    @Override
    public void onOpen() {
        // If null, load from config
        if (this.gameInfo == null) {
            this.gameInfo = SakuyaBridge.INSTANCE.getConfig().getLastGameInfo();

            // If still null, create new
            if (this.gameInfo == null) {
                this.gameInfo = new GameInfo();
            }
        }

        loadData();
        updateComponents();
    }

    /**
     * Determines if everything mandatory is selected
     */
    private boolean isEverythingSelected() {
        if (!isGameNameValid()) {
            return false;
        }

        if (!isPasswordValid()) {
            return false;
        }

        if (comboBoxRegion.getSelectedItem() == null) {
            return false;
        }

        if (comboBoxGameVersion.getSelectedItem() == null) {
            return false;
        }

        if (comboBoxGameTechnology.getSelectedItem() == null) {
            return false;
        }

        if (comboBoxGamePlayerSide.getSelectedItem() == null) {
            return false;
        }

        return true;
    }
}
