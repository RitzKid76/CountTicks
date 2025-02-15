package org.ritzkid76.CountTicks.Commands;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class PulseCommand extends ThreadCommand {
	private BlockVector3 position;

	public PulseCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(args.length > 0) {
			endedTask();
			return true;
		}

		if(executionCancelCheck())
			return true;

		position = playerData.getFirstPosition();
		if(position == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}

		return false;
	}

	private void timePulse(BlockVector3 pos, BlockState startState, long startTicks) {
		LongWrapper timeProgress = new LongWrapper(startTicks);

		startedTask(Bukkit.getScheduler().runTaskTimer(
			plugin,
			() -> {
				long currentTime = System.currentTimeMillis();
				long difference = currentTime - timeProgress.value;
				if(difference > 5000L) {
					timeProgress.value = currentTime;
					MessageSender.sendMessage(player, Message.PULSING_PROGRESS, getFormattedTicks(player.getWorld().getGameTime() - startTicks));
				}

				if(startState.equals(BlockGetter.blockStateFromBlockVector3(player.getWorld(), pos)))
					return;

				endedTask(true);
				long totalTicks = player.getWorld().getGameTime() - startTicks;
				MessageSender.sendMessage(player, Message.PULSE, getFormattedTicks(totalTicks));
			},
			0, 1
		));
	}

	@Override
	public void verifiedExecute() {
		MessageSender.sendMessage(player, Message.PULSE_WAITING);

		BlockState startState = BlockGetter.blockStateFromBlockVector3(player.getWorld(), position);

		long startTime = System.currentTimeMillis();
		startedTask(Bukkit.getScheduler().runTaskTimer(
			playerData.getPlugin(),
			() -> {
				long currentTime = System.currentTimeMillis();
				if(currentTime - startTime > 10000L) {
					MessageSender.sendMessage(player, Message.PULSE_TIMEOUT);
					endedTask(true);
					return;
				}

				BlockState newState = BlockGetter.blockStateFromBlockVector3(player.getWorld(), position);
				if(startState.equals(newState))
					return;

				terminate(true);
				MessageSender.sendMessage(player, Message.START_PULSE_MODE);
				timePulse(position, newState, player.getWorld().getGameTime());
			},
			0, 1
		));
	}

	@Override
	public Message noActiveTask() {
		return Message.NO_ACTIVE_PULSING;
	}

	@Override
	public Message stopMode() {
		return Message.STOP_PULSE_MODE;
	}

	@Override
	public Message currentlyExecuting() {
		return Message.CURRENTLY_PULSING;
	}

	@Override
	public Message alreadyExecuting() {
		return Message.ALREADY_PULSING;
	}

	@Override
	public Message overriden() {
		return Message.PULSE_OVERRIDEN;
	}
}
