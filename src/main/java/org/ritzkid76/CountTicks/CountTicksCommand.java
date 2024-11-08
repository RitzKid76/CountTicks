package org.ritzkid76.CountTicks;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.PlayerData.PlayerDataContainer;
import org.ritzkid76.CountTicks.PlayerData.PlayerEventListener;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;

import io.github.pieter12345.javaloader.bukkit.BukkitCommand;
import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProject;
import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProjectPlugin;

public class CountTicksCommand extends JavaLoaderBukkitProject {
	public World world;
	public BukkitScheduler scheduler;

	@Override
	public void onLoad() {
		enableListeners();

		File dataFolder = getPlugin().getDataFolder();

		ArgumentParser.setDataFolder(dataFolder);
		ArgumentParser.setPlugin(getPlugin());
		MessageSender.populateOptions(dataFolder);

		scheduler = getPlugin().getServer().getScheduler();

		MessageSender.sendConsoleMessage(Message.LOADED);
	}

	@Override
	public void onUnload() {
		disableListeners();

		scheduler.cancelTasks(getPlugin());

		MessageSender.sendConsoleMessage(Message.UNLOADED);
	}

	private void addListener(Listener l) {
		JavaLoaderBukkitProjectPlugin plugin = getPlugin();
		plugin.getServer().getPluginManager().registerEvents(l, plugin);
	}

	private void enableListeners() {
		addListener(new PlayerEventListener());
	}
	private void disableListeners() {
		HandlerList.unregisterAll(getPlugin());
	}

	@Override
	public String getVersion() {
		return "0.0.1-SNAPSHOT";
	}

	public static void sendChatMessage(CommandSender sender, String message) {
		sender.sendMessage(message);
	}
	public static void sendChatMessage(CommandSender sender, Message message) {
		sender.sendMessage(message.get());
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sendChatMessage(sender, Message.CONSOLE_USE);
			return true;
		}

		PlayerData playerData = PlayerDataContainer.get(player);
		ArgumentParser.run(args, playerData, label);

		return true; // dont want to use the bukkit default useage
	}

	@Override
	public List<String> onTabComplete(
		CommandSender sender,
		Command command,
		String label,
		String[] args
	) {
		return ArgumentParser.onTabComplete(sender, command, label, args);
	}

	@Override
	public BukkitCommand[] getCommands() {
		return new BukkitCommand[]{
				new BukkitCommand("countticks")
						.setUsageMessage("this should not happen .,;,;,.")
						.setPermission("javaloader.countticks.command")
						.setPermissionMessage("You do not have permission to use this command.")
						.setAliases("ct")
						.setExecutor(this)
						.setTabCompleter(this)
		};
	}
}
