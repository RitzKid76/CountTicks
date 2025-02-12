package org.ritzkid76.CountTicks.Commands;

import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public abstract class ThreadCommand extends Command {
	public BukkitTask task;
	
	public ThreadCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}
	
	public boolean isRunning() {
		if(task == null) {
			return false;
		}
		return !task.isCancelled();
	}

	public void override() {
		endedTask(true);

		MessageSender.sendMessage(player, overriden());
	}

	public void terminate(boolean silent) {
		if(!isRunning()) {
			if(!silent)
				MessageSender.sendMessage(player, noActiveTask());
			return;
		}

		task.cancel();
		task = null;
		
		if(!silent)
			MessageSender.sendMessage(player, stopMode());
	}

	public void startedTask(BukkitTask bt) {
		task = bt;
	}

	public void endedTask() {
		endedTask(false);
	}
	public void endedTask(boolean silent) {
		playerData.clearThreadCommand(silent);
	}

	public boolean shouldLinkToCurrentThread() {
		return args.length > 0;
	}

	public void link(String[] args) {
		this.args = args;
		execute();
	}

	public boolean isAlreadyExecuting(ThreadCommand newThreadCommand) {
		return getClass() == newThreadCommand.getClass();
	}
	
	public abstract Message noActiveTask();
	public abstract Message stopMode();
	public abstract Message currentlyExecuting();
	public abstract Message alreadyExecuting();
	public abstract Message overriden();

	protected class LongWrapper {
		long value;
		LongWrapper(long l) {
			value = l;
		}
	}

	public static String getFormattedTimer(long difference) {
		double seconds = (double) difference / 1000.0;
		return String.format("%.2f", seconds);
	}
	public static String getFormattedTicks(long difference) {
		return String.valueOf(difference/2);
	}
}
