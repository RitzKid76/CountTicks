package org.ritzkid76.CountTicks.Message;

import org.bukkit.ChatColor;

// JavaLoader does not extend , so i made this instead :P
public enum Message {
    LOADED(MessageType.INFO, ChatColor.GREEN + "Loaded."),
    UNLOADED(MessageType.INFO, ChatColor.RED + "Unloaded."),

    CONSOLE_USE(MessageType.ERROR, "This command can only be used by players!"),
    NO_SELECTION(MessageType.ERROR, "Please select a start point (pos1) and end point (pos2).");

    private final String message;
    private final MessageType type;

    private static final String commandPrefix =
        ChatColor.BLUE + "[" +
        ChatColor.AQUA + "CountTicks" +
        ChatColor.BLUE + "] " +
        ChatColor.RESET;
    private static final String warning =
        ChatColor.GOLD + "[" +
        ChatColor.YELLOW + "WARNING" +
        ChatColor.GOLD + "] " +
        ChatColor.RESET;
    private static final String error =
        ChatColor.DARK_RED + "[" +
        ChatColor.RED + "ERROR" +
        ChatColor.DARK_RED + "] " +
        ChatColor.RESET;

    Message(MessageType mt, String m) {
        type = mt;
        message = m;
    }

    public String get() { return commandPrefix + formatMessage(); }

    private String formatMessage() {
        switch(type) {
            case INFO -> { return formatInfo(); }
            case WARNING -> { return formatWarning(); }
            case ERROR -> { return formatError(); }
        }
        return null;
    }

    private String formatInfo() { return message; }
    private String formatWarning() { return warning + message; }
    private String formatError() { return error + message; }
}
