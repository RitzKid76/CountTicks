package org.ritzkid76.CountTicks.Commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public abstract class Command {
	public BukkitTask commandTask;
	protected final String[] args;
	protected final PlayerData playerData;
	protected final String label;
	protected final SyntaxHandler syntaxHandler;
	protected final Player player;

	public Command(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		this.args = args;
		this.playerData = playerData;
		this.label = label;
		this.syntaxHandler = syntaxHandler;
		
		player = playerData.getPlayer();
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
