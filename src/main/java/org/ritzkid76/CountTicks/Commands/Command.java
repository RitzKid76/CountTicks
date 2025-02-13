package org.ritzkid76.CountTicks.Commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public abstract class Command {
	public BukkitTask commandTask;
	protected String[] args;
	protected final PlayerData playerData;
	protected final String label;
	protected final SyntaxHandler syntaxHandler;
	protected final Player player;
	protected final Plugin plugin;

	public Command(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		this.args = args;
		this.playerData = playerData;
		this.label = label;
		this.syntaxHandler = syntaxHandler;

		player = playerData.getPlayer();
		plugin = playerData.getPlugin();
	}

	public String[] getArgs() {
		return args;
	}

	public void execute() {
		if(cancelationScreening())
			return;
			
		verifiedExecute();
	}

	public boolean cancelationScreening() {
		return false;
	}

	public abstract void verifiedExecute();
}
