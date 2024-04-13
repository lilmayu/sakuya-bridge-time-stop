package dev.mayuna.sakuyabridge.client.v1.ui.utils;

import net.miginfocom.swing.MigLayout;

public class MigLayoutUtils {

    /**
     * Creates empty MigLayout
     *
     * @return MigLayout
     */
    public static MigLayout create() {
        return new MigLayout();
    }

    /**
     * Creates MigLayout
     *
     * @param colConstraints Column constraints
     *
     * @return MigLayout
     */
    public static MigLayout create(String colConstraints) {
        return new MigLayout("", colConstraints);
    }

    /**
     * Creates MigLayout
     *
     * @param colConstraints Column constraints
     *
     * @return MigLayout
     */
    public static MigLayout createNoInsets(String colConstraints) {
        return new MigLayout("insets 0", colConstraints);
    }

    /**
     * Creates default MigLayout that I use
     *
     * @return MigLayout
     */
    public static MigLayout createGrow() {
        return new MigLayout("", "[grow]", "");
    }

    /**
     * Creates MigLayout with "[grow][grow]" as colConstraints
     *
     * @return MigLayout
     */
    public static MigLayout createGrowGrow() {
        return new MigLayout("", "[grow][grow]", "");
    }

    /**
     * Creates MigLayout with "[grow,shrink]" as colConstraints
     *
     * @return MigLayout
     */
    public static MigLayout createGrowAndShrink() {
        return new MigLayout("", "[grow][shrink]", "");
    }

    /**
     * Creates MigLayout with "[grow][shrink][shrink]" as colConstraints
     *
     * @return MigLayout
     */
    public static MigLayout createGrowAndShrinkShrink() {
        return new MigLayout("", "[grow][shrink][shrink]", "");
    }

    /**
     * Creates MigLayout with "[shrink][grow]" as colConstraints
     *
     * @return MigLayout
     */
    public static MigLayout createShrinkAndGrow() {
        return new MigLayout("", "[shrink][grow]", "");
    }
}
