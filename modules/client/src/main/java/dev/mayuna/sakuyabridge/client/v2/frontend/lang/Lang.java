package dev.mayuna.sakuyabridge.client.v2.frontend.lang;

public final class Lang {

    private Lang() {
    }

    public static final class General {

        public static final String TITLE = "general.title";
        public static final String THEME_DARK = "general.theme.dark";
        public static final String THEME_LIGHT = "general.theme.light";
    }

    public static final class Frames {

        public static final class Connect {

            public static final String BUTTON_EXIT = "frames.connect.button.exit";
            public static final String BUTTON_CONNECT = "frames.connect.button.connect";
            public static final String BUTTON_OFFLINE = "frames.connect.button.offline";
            public static final String LABEL_AUTHOR = "frames.connect.label.author";
            public static final String LABEL_VERSION = "frames.connect.label.version";
            public static final String WEBSITE_TOOLTIP = "frames.connect.tooltip.website";
            public static final String RELEASE_TOOLTIP = "frames.connect.tooltip.releases";
            public static final String LABEL_TITLE = "frames.connect.label.title";
            public static final String LABEL_SUBTITLE = "frames.connect.label.subtitle";
            public static final String LABEL_DESCRIPTION = "frames.connect.label.description";
            public static final String LABEL_SERVER_ADDRESS = "frames.connect.label.server_address";
            public static final String PLACEHOLDER_SERVER_ADDRESS = "frames.connect.placeholder.server_address";
        }

        public static final class Main {

            public static final String TITLE_CONFIRM_CLOSE = "frames.main.title.confirm_close";
            public static final String TEXT_CONFIRM_CLOSE = "frames.main.text.confirm_close";
        }
    }
}
