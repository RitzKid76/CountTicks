package org.ritzkid76.CountTicks;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.PlayerData.PlayerDataContainer;
import org.ritzkid76.CountTicks.PlayerData.PlayerEventListener;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;
import org.ritzkid76.CountTicks.SyntaxHandling.UsageGenerator;

import io.github.pieter12345.javaloader.bukkit.BukkitCommand;
import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProject;
import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProjectPlugin;

public class CountTicksCommand extends JavaLoaderBukkitProject {
	private SyntaxHandler syntaxHandler;
	private PlayerDataContainer playerDataContainer;
	private ArgumentParser parser;
	private UsageGenerator usageGenerator;

	public World world;

	@Override
	public void onLoad() {
		enableListeners();

		File dataFolder = getPlugin().getDataFolder();
		syntaxHandler = new SyntaxHandler(dataFolder);
		usageGenerator = new UsageGenerator(syntaxHandler);
		parser = new ArgumentParser(syntaxHandler, usageGenerator);

		MessageSender.populateOptions(dataFolder);

		MessageSender.sendConsoleMessage(Message.LOADED);
	}

	@Override
	public void onUnload() {
		disableListeners();

		PlayerDataContainer.shutdown();

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
	public static void sendChatMessage(CommandSender sender, Message message) { sender.sendMessage(message.get()); }

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sendChatMessage(sender, Message.CONSOLE_USE);
			return true;
		}

		PlayerData playerData = PlayerDataContainer.get(player);
		return parser.run(args, playerData);
	}

	// i am aware that there is a .setTabCompleter() function, but i prefer this for now. i can change it later
	@Override
	public List<String> onTabComplete(
		CommandSender sender,
		Command command,
		String label,
		String[] args
	) {
		return syntaxHandler.onTabComplete(sender, command, label, args);
	}

	@Override
	public BukkitCommand[] getCommands() {
		return new BukkitCommand[]{
				new BukkitCommand("countticks")
						.setUsageMessage("TODO")
						.setPermission("javaloader.countticks.counttickscommand")
						.setPermissionMessage("You do not have permission to use this command.")
						.setAliases("ct")
						.setExecutor(this)
						.setTabCompleter(this)
		};
	}
}
