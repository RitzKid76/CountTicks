package org.ritzkid76.CountTicks.Message;

import org.bukkit.ChatColor;

public enum Message {
	LOADED(MessageType.INFO),
	UNLOADED(MessageType.INFO),

	CONSOLE_USE(MessageType.ERROR),
	INVALID_SYNTAX(MessageType.ERROR, 2),
	HELP(MessageType.INFO),
	HELP_LISTING(MessageType.INFO, 2),
	STOPPED_ALL(MessageType.INFO),

	NO_START_SELECTED(MessageType.ERROR),
	NO_END_SELECTED(MessageType.ERROR),
	NO_SCANNED_BUILD(MessageType.ERROR, 1),
	NO_SCAN_REGION(MessageType.ERROR, 1),
	NON_CUBOID_REGION(MessageType.ERROR),

	START_CHANGED(MessageType.WARNING),
	REGION_CHANGED(MessageType.WARNING),
	SET_SCAN_REGION(MessageType.INFO, 6),

	SCAN_COMPLETE(MessageType.INFO, 2),

	START_SCAN(MessageType.INFO),
	STOP_SCAN(MessageType.INFO),
	NO_ACTIVE_SCAN(MessageType.INFO),
	ALREADY_SCANNING(MessageType.INFO),
	CURRENTLY_SCANNING(MessageType.INFO, 1),
	INVALID_START(MessageType.ERROR),
	SCAN_PROGRESS(MessageType.INFO, 1),

	START_INSPECT_MODE(MessageType.INFO),
	STOP_INSPECT_MODE(MessageType.INFO),
	NO_ACTIVE_INSPECTION(MessageType.INFO),
	ALREADY_INSPECTING(MessageType.INFO),
	CURRENTLY_INSPECTING(MessageType.INFO, 1),

	DELAY(MessageType.INFO, 1),
	DELAY_SHORT(MessageType.INFO, 1),
	UNSCANNED_LOCATION(MessageType.ERROR),
	UNSCANNED_LOCATION_SHORT(MessageType.INFO),
	OUT_OF_BOUNDS(MessageType.ERROR),
	OUT_OF_BOUNDS_SHORT(MessageType.INFO);



	private final String message;
	private final MessageType type;
	private final int parameterCount;

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
		this(mt, 0);
	}
	Message(MessageType mt, int pc) {
		type = mt;
		parameterCount = pc;
		message = MessageSender.getMessage(name().toLowerCase());
	}

	public int parameterCount() {
		return parameterCount;
	}
	public String get() {
		return commandPrefix + formatMessage();
	}
	public String getClean() {
		return formatMessage();
	}

	private String formatMessage() {
		return switch(type) {
			case INFO -> formatInfo();
			case WARNING -> formatWarning();
			case ERROR -> formatError();
		};
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
