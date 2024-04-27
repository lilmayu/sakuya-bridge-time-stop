package dev.mayuna.sakuyabridge.client.v2.frontend.lang;

public final class Lang {

    private Lang() {
    }

    public static final class General {

        public static final String TEXT_TITLE = "general.text.title";
        public static final String TEXT_THEME_DARK = "general.text.theme.dark";
        public static final String TEXT_THEME_LIGHT = "general.text.theme.light";
        public static final String TEXT_ERROR = "general.text.error";
        public static final String TEXT_WARNING = "general.text.warning";
        public static final String TEXT_INFORMATION = "general.text.information";
        public static final String TEXT_SUCCESS = "general.text.success";
        public static final String TEXT_QUESTION = "general.text.question";
        public static final String TEXT_EXIT = "general.text.exit";
    }

    public static final class Frames {

        public static final class Logger {

            public static final String COLUMN_TIME = "frames.logger.column.time";
            public static final String COLUMN_LEVEL = "frames.logger.column.level";
            public static final String COLUMN_SOURCE = "frames.logger.column.source";
            public static final String COLUMN_MESSAGE = "frames.logger.column.message";
            public static final String BUTTON_COPY_LOGS = "frames.logger.button.copy_logs";
            public static final String TEXT_TITLE = "frames.logger.text.title";
            public static final String TEXT_LOGS_COPIED_TO_CLIPBOARD = "frames.logger.text.logs_copied_to_clipboard";
            public static final String TEXT_PERSONAL_INFORMATION_WARNING = "frames.logger.text.personal_information_warning";
        }

        public static final class LogInfo {

            public static final String TEXT_TITLE = "frames.log_info.text.title";
            public static final String BUTTON_COPY_LOG = "frames.log_info.button.copy_log";
            public static final String TEXT_LOG_COPIED_TO_CLIPBOARD = "frames.log_info.text.log_copied_to_clipboard";
        }

        public static final class Connect {

            public static final String BUTTON_CONNECT = "frames.connect.button.connect";
            public static final String BUTTON_OFFLINE = "frames.connect.button.offline";
            public static final String LABEL_AUTHOR = "frames.connect.label.author";
            public static final String LABEL_VERSION = "frames.connect.label.version";
            public static final String WEBSITE_TOOLTIP = "frames.connect.tooltip.website";
            public static final String RELEASE_TOOLTIP = "frames.connect.tooltip.releases";
            public static final String TEXT_LABEL_TITLE = "frames.connect.label.text.title";
            public static final String LABEL_SUBTITLE = "frames.connect.label.subtitle";
            public static final String LABEL_DESCRIPTION = "frames.connect.label.description";
            public static final String LABEL_SERVER_ADDRESS = "frames.connect.label.server_address";
            public static final String PLACEHOLDER_SERVER_ADDRESS = "frames.connect.placeholder.server_address";
            public static final String TEXT_CONNECTING = "frames.connect.text.connecting";
            public static final String TEXT_FAILED_TO_CONNECT = "frames.connect.text.failed_to_connect";
            public static final String TEXT_FAILED_TO_PREPARE_CLIENT = "frames.connect.text.failed_to_prepare_client";
            public static final String TEXT_CHECKING_VERSION = "frames.connect.text.checking_version";
            public static final String TEXT_FAILED_TO_EXCHANGE_VERSIONS = "frames.connect.text.failed_to_exchange_versions";
            public static final String TEXT_FETCHING_SERVER_INFO = "frames.connect.text.fetching_server_info";
            public static final String TEXT_FAILED_TO_FETCH_SERVER_INFO = "frames.connect.text.failed_to_fetch_server_info";
        }

        public static final class ServerInfo {

            public static final String TEXT_TITLE = "frames.server_info.text.title";
            public static final String LABEL_DESCRIPTION = "frames.server_info.label.description";
            public static final String LABEL_SERVER_NAME = "frames.server_info.label.server_name";
            public static final String LABEL_SERVER_MAINTAINER = "frames.server_info.label.server_maintainer";
            public static final String LABEL_SERVER_REGION = "frames.server_info.label.server_region";
            public static final String LABEL_MOTD = "frames.server_info.label.motd";
            public static final String LABEL_AUTHENTICATE = "frames.server_info.label.authenticate";
            public static final String BUTTON_CONTINUE_IN_PREVIOUS_SESSION_NO_SESSION = "frames.server_info.button.continue_in_previous_session.no_session";
            public static final String BUTTON_CONTINUE_IN_PREVIOUS_SESSION = "frames.server_info.button.continue_in_previous_session";
            public static final String BUTTON_CONTINUE_IN_PREVIOUS_SESSION_DISABLED = "frames.server_info.button.continue_in_previous_session_disabled";
            public static final String BUTTON_AUTH_DISCORD = "frames.server_info.button.auth_discord";
            public static final String BUTTON_AUTH_USERNAME_PASSWORD = "frames.server_info.button.auth_username_password";
            public static final String BUTTON_AUTH_ANONYMOUS = "frames.server_info.button.auth_anonymous";
            public static final String BUTTON_DISCONNECT = "frames.server_info.button.disconnect";
            public static final String TOOLTIP_CONTINUE_IN_PREVIOUS_SESSION = "frames.server_info.tooltip.continue_in_previous_session";
            public static final String TOOLTIP_AUTH_DISCORD = "frames.server_info.tooltip.auth_discord";
            public static final String TOOLTIP_AUTH_USERNAME_PASSWORD = "frames.server_info.tooltip.auth_username_password";
            public static final String TOOLTIP_AUTH_ANONYMOUS = "frames.server_info.tooltip.auth_anonymous";
            public static final String TEXT_TITLE_DISCONNECT = "frames.server_info.text.title.disconnect";
            public static final String TEXT_CONFIRM_DISCONNECT = "frames.server_info.text.confirm_disconnect";
            public static final String TEXT_LOGGING_IN_WITH_SESSION_TOKEN = "frames.server_info.text.logging_in_with_session_token";
            public static final String TEXT_FAILED_TO_LOGIN_WITH_SESSION_TOKEN = "frames.server_info.text.failed_to_login_with_session_token";
            public static final String TEXT_TITLE_FAILED_TO_LOGIN_WITH_SESSION_TOKEN = "frames.server_info.text.title.failed_to_login_with_session_token";
            public static final String TEXT_FETCHING_CURRENT_USER = "frames.server_info.text.fetching_current_user";
            public static final String TEXT_FAILED_TO_FETCH_CURRENT_USER = "frames.server_info.text.failed_to_fetch_current_user";
        }

        public static final class Auth {

            public static final class UsernamePassword {

                public static final String TEXT_TITLE = "frames.username_password_auth.text.title";
                public static final String BUTTON_LOGIN = "frames.username_password_auth.button.login";
                public static final String BUTTON_NO_ACCOUNT_REGISTER = "frames.username_password_auth.button.no_account_register";
                public static final String BUTTON_REGISTER = "frames.username_password_auth.button.register";
                public static final String BUTTON_CANCEL = "frames.username_password_auth.button.cancel";
                public static final String LABEL_USERNAME = "frames.username_password_auth.label.username";
                public static final String LABEL_PASSWORD = "frames.username_password_auth.label.password";
                public static final String LABEL_PASSWORD_AGAIN = "frames.username_password_auth.label.password_again";
                public static final String TEXT_PASSWORD_EMPTY = "frames.username_password_auth.text.password_empty";
                public static final String TEXT_PASSWORD_SHORT = "frames.username_password_auth.text.password_short";
                public static final String TEXT_PASSWORD_MISMATCH = "frames.username_password_auth.text.password_mismatch";
                public static final String TEXT_USERNAME_EMPTY = "frames.username_password_auth.text.username_empty";
                public static final String TEXT_USERNAME_SHORT = "frames.username_password_auth.text.username_short";
                public static final String TEXT_LOGIN_FAILED = "frames.username_password_auth.text.login_failed";
                public static final String TEXT_REGISTER_FAILED = "frames.username_password_auth.text.register_failed";
                public static final String TEXT_LOGGING_IN = "frames.username_password_auth.text.logging_in";
                public static final String TEXT_REGISTERING = "frames.username_password_auth.text.registering";
            }
        }

        public static final class Main {

            public static final String TEXT_TITLE_CONFIRM_CLOSE = "frames.main.text.title.confirm_close";
            public static final String TEXT_CONFIRM_CLOSE = "frames.main.text.confirm_close";
        }
    }
}
