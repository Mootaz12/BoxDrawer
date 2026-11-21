package com.boxdrawer.gateway;

import java.util.Map;

/** HTML error page shown to browsers for every error going through the gateway. */
final class ErrorPage {

    private static final Map<Integer, String[]> TEXTS = Map.of(
            400, new String[]{"Bad request", "The request could not be understood. Check the link and try again."},
            401, new String[]{"Sign in required", "You need to be signed in to see this page."},
            403, new String[]{"Access denied", "You are not allowed to see this page."},
            404, new String[]{"Not found", "The board or page you are looking for does not exist."},
            410, new String[]{"This link is no longer valid",
                    "Share links work only once. Ask the person who shared the board to send it again."},
            503, new String[]{"Service unavailable", "BoxDrawer is starting or busy. Please try again in a moment."});

    private static final String[] CLIENT_ERROR = {"Something is wrong with the request", "Check the link and try again."};
    private static final String[] SERVER_ERROR = {"Something went wrong", "An unexpected error happened. Please try again later."};

    private static final String TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>BoxDrawer - %1$d</title>
            <style>
            :root { --bg: #f6f7f9; --card: #fff; --text: #1e1e1e; --muted: #5f6368; --accent: #6965db; }
            @media (prefers-color-scheme: dark) {
              :root { --bg: #121212; --card: #1e1e1e; --text: #e8e8e8; --muted: #a0a0a0; --accent: #a8a5ff; }
            }
            body { margin: 0; min-height: 100vh; display: grid; place-items: center; padding: 16px;
                   box-sizing: border-box; background: var(--bg); color: var(--text);
                   font-family: system-ui, -apple-system, "Segoe UI", sans-serif; }
            main { background: var(--card); max-width: 440px; width: 100%%; padding: 40px 32px;
                   border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, .08); text-align: center; }
            .code { font-size: 56px; font-weight: 700; color: var(--accent); margin: 0; }
            h1 { font-size: 20px; margin: 8px 0 12px; }
            p { color: var(--muted); line-height: 1.5; margin: 0; }
            .brand { margin-top: 28px; font-size: 13px; }
            </style>
            </head>
            <body>
            <main>
            <p class="code">%1$d</p>
            <h1>%2$s</h1>
            <p>%3$s</p>
            <p class="brand">BoxDrawer</p>
            </main>
            </body>
            </html>
            """;

    private ErrorPage() {
    }

    static String render(int status) {
        String[] text = TEXTS.getOrDefault(status, status < 500 ? CLIENT_ERROR : SERVER_ERROR);
        return TEMPLATE.formatted(status, text[0], text[1]);
    }
}
