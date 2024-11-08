package org.ritzkid76.CountTicks.Message;

import org.bukkit.ChatColor;

public enum Message {
	LOADED(MessageType.INFO),
	UNLOADED(MessageType.INFO),

	HELP(MessageType.INFO),
	CONSOLE_USE(MessageType.ERROR),
	INVALID_SYNTAX(MessageType.ERROR),

	SET_SCAN_REGION(MessageType.INFO),
	SCAN_COMPLETE(MessageType.INFO),
	STOP_SCAN(MessageType.INFO),
	SCAN_IN_PROGRESS(MessageType.INFO),
	START_SCAN(MessageType.INFO),
	UNSCANNED_LOCATION_SHORT(MessageType.INFO),
	OUT_OF_BOUNDS_SHORT(MessageType.INFO),
	ALREADY_SCANNING(MessageType.ERROR),
	NO_ACTIVE_SCAN(MessageType.ERROR),
	NO_SCANNED_BUILD(MessageType.ERROR),
	NO_SCAN_REGION(MessageType.ERROR),
	NO_START_SELECTED(MessageType.ERROR),
	INVALID_START(MessageType.ERROR),
	UNSCANNED_LOCATION(MessageType.ERROR),
	OUT_OF_BOUNDS(MessageType.ERROR),
	CURRENTLY_SCANNING(MessageType.ERROR),

	START_INSPECT_MODE(MessageType.INFO),
	STOP_INSPECT_MODE(MessageType.INFO),
	DELAY(MessageType.INFO),
	DELAY_SHORT(MessageType.INFO),
	NO_ACTIVE_INSPECTION(MessageType.ERROR),
	ALREADY_INSPECTING(MessageType.ERROR),
	CURRENTLY_INSPECTING(MessageType.ERROR),

	NO_END_SELECTED(MessageType.ERROR),
	START_CHANGED(MessageType.WARNING);



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

	Message(MessageType mt) {
		type = mt;
		message = MessageSender.getMessage(name().toLowerCase());
	}

	public String get() {
		return commandPrefix + formatMessage();
	}
	public String getClean() {
		return formatMessage();
	}

	private String formatMessage() {
		switch(type) {
			case INFO -> {
				return formatInfo();
			}
			case WARNING -> {
				return formatWarning();
			}
			case ERROR -> {
				return formatError();
			}
		}
		return null;
	}

	private String formatInfo() {
		return message;
	}
	private String formatWarning() {
		return warning + message;
	}
	private String formatError() {
		return error + message;
	}
}
