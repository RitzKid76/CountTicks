package org.ritzkid76.CountTicks;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Debug {
    private static final String debugPrefix =
        ChatColor.GOLD + "[" +
        ChatColor.YELLOW + "DEBUG" +
        ChatColor.GOLD + "] " +
        ChatColor.RESET;
    public static void log(String message) { log(message, ""); }
    public static void log(String message, String name) { log(Objects.requireNonNull(Bukkit.getPlayer("RitzKid76")), message, name); }
    public static void log(CommandSender sender, String message, String name) { sender.sendMessage(debugPrefix + name + ": " + message); }
}
