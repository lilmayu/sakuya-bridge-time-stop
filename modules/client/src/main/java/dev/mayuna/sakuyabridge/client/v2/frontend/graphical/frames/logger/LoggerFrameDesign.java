package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.logging.LoggerFrameLogHandler;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Design for the logger frame
 */
public abstract class LoggerFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JButton buttonExit;
    protected JButton buttonCopy;

    public JTableCinnamonRoll tableLogs;

    @Override
    protected void prepareFrame(Component component) {
        this.setTitle($getTranslation(Lang.Frames.Logger.TEXT_TITLE));
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareTitle();
        prepareBody();
        prepareFooter();

        this.pack();
        this.setSize(800, 600);
        this.setLocationRelativeTo(component);

        this.getContentPane().requestFocusInWindow();
    }

    @Override
    protected void prepareComponents() {
        buttonExit = new JButton($getTranslation(Lang.General.TEXT_EXIT));
        buttonCopy = new JButton($getTranslation(Lang.Frames.Logger.BUTTON_COPY_LOGS));

        tableLogs = new JTableCinnamonRoll();
        tableLogs.setTableCellRenderer(new LoggerFrameLogHandler.LoggerTableRenderer());
        tableLogs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableLogs.setModel(LoggerFrameLogHandler.TABLE_MODEL);
        tableLogs.setFillsViewportHeight(true);
        tableLogs.setShowGrid(true);
        tableLogs.setGridColor(tableLogs.getBackground().darker());
        tableLogs.setFont(CinnamonRoll.createMonospacedFont(12));
        tableLogs.getTableHeader().setFont(CinnamonRoll.createMonospacedFont(12));
        tableLogs.getTableHeader().setReorderingAllowed(false);
        tableLogs.getTableHeader().setResizingAllowed(true);
        tableLogs.setRowSelectionAllowed(false);
        tableLogs.setColumnSelectionAllowed(false);
        tableLogs.setAutoscrolls(false);
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonExit, this::clickExit);
        CinnamonRoll.onClick(buttonCopy, this::clickCopy);

        tableLogs.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                clickRowTableLogs(e);
            }
        });
    }

    protected abstract void clickExit(MouseEvent mouseEvent);

    protected abstract void clickCopy(MouseEvent mouseEvent);

    protected abstract void clickRowTableLogs(MouseEvent mouseEvent);

    /**
     * Prepares the title of the frame
     */
    private void prepareTitle() {
        JPanel panelTitle = new JPanel();
        panelTitle.setLayout(MigLayoutUtils.createGrow());

        JLabel labelTitle = new JLabel($getTranslation(Lang.Frames.Logger.TEXT_TITLE));
        CinnamonRoll.deriveFontWith(labelTitle, Font.BOLD, 20);
        panelTitle.add(labelTitle, "wrap");

        this.add(panelTitle, "dock north");
    }

    /**
     * Prepares the body of the frame
     */
    private void prepareBody() {
        JPanel panelBody = new JPanel();
        panelBody.setLayout(MigLayoutUtils.createGrow());

        panelBody.add(new JScrollPane(tableLogs), "dock center");

        this.add(panelBody, "dock center");
    }

    private void prepareFooter() {
        JPanel panelFooter = new JPanel();
        panelFooter.setLayout(MigLayoutUtils.create("[shrink][shrink][grow][shrink][shrink]"));

        panelFooter.add(buttonExit, "bottom");
        panelFooter.add(new JSeparator(SwingConstants.VERTICAL), "growy");
        panelFooter.add(buttonCopy);

        this.add(panelFooter, "dock south");
    }
}
