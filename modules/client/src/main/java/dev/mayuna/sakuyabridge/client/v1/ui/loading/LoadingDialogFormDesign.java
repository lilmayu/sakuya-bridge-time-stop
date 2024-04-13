package dev.mayuna.sakuyabridge.client.v1.ui.loading;

import dev.mayuna.sakuyabridge.client.v1.ui.forms.BaseFormDesign;
import dev.mayuna.sakuyabridge.client.v1.ui.utils.MigLayoutUtils;

import javax.swing.*;
import java.awt.*;

public abstract class LoadingDialogFormDesign extends BaseFormDesign {

    protected JProgressBar progressBar;
    protected JLabel progressInfo;

    public LoadingDialogFormDesign(Component parent) {
        super(parent);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Loading...");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setResizable(false);

        this.setLayout(MigLayoutUtils.createGrow());

        prepareTitle();
        prepareProgressBar();
        prepareProgressInfo();

        this.pack();
        this.setSize(300, this.getHeight());
        this.setLocationRelativeTo(parent);
    }

    @Override
    protected void prepareComponents() {
        progressBar = new JProgressBar();
        progressInfo = new JLabel();
    }

    @Override
    protected void registerListeners() {
    }

    private void prepareTitle() {
        JLabel title = new JLabel("Loading, please wait.");
        title.setFont(title.getFont().deriveFont(20f));
        this.add(title, "wrap");
    }

    private void prepareProgressBar() {
        progressBar.setIndeterminate(true);
        this.add(progressBar, "growx, wrap");
        progressBar.setMinimumSize(new Dimension(this.getWidth(), 12));
    }

    private void prepareProgressInfo() {
        progressInfo.setText("Loading...");
        this.add(progressInfo);
    }
}
