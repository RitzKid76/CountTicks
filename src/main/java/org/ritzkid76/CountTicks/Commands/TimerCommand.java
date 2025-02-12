package org.ritzkid76.CountTicks.Commands;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class TimerCommand extends ThreadCommand {
	private BlockVector3 startPosition;
	private BlockVector3 endPosition;

	public TimerCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(args.length > 0) {
			endedTask();
			return true;
		}

		if(playerData.executionCancelCheck(label))
			return true;

		startPosition = playerData.getFirstPosition();
		if(startPosition == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}
		endPosition = playerData.getSecondPosition();
		if(endPosition == null) {
			MessageSender.sendMessage(player, Message.NO_END_SELECTED);
			return false;
		}

		return false;
	}

	private void timeTicks(BlockVector3 pos, BlockState startState, long startTicks) {
		LongWrapper timeProgress = new LongWrapper(startTicks);

		startedTask(Bukkit.getScheduler().runTaskTimer(
			plugin,
			() -> {
				long currentTime = System.currentTimeMillis();
				long difference = currentTime - timeProgress.value;
				if(difference >= 5000L) {
					timeProgress.value = currentTime;
					MessageSender.sendMessage(player, Message.TIMING_PROGRESS, getFormattedTicks(player.getWorld().getGameTime() - startTicks));
				}

				if(startState.equals(BlockGetter.blockStateFromBlockVector3(player.getWorld(), pos)))
					return;

				endedTask(true);
				long totalTicks = player.getWorld().getGameTime() - startTicks;
				MessageSender.sendMessage(player, Message.DELAY, getFormattedTicks(totalTicks));
			},
			0, 1
		));
	}

	@Override
	public void verifiedExecute() {
		MessageSender.sendMessage(player, Message.TIMER_WAITING);

		BlockState startPosState = BlockGetter.blockStateFromBlockVector3(player.getWorld(), startPosition);
		BlockState endPosState = BlockGetter.blockStateFromBlockVector3(player.getWorld(), endPosition);

		long startTime = System.currentTimeMillis();
		startedTask(Bukkit.getScheduler().runTaskTimer(
			plugin,
			() -> {
				long currentTime = System.currentTimeMillis();
				if(currentTime - startTime > 10000L) {
					MessageSender.sendMessage(player, Message.TIMER_TIMEOUT);
					endedTask(true);
					return;
				}
				
				if(startPosState.equals(BlockGetter.blockStateFromBlockVector3(player.getWorld(), startPosition)))
					return;
				
				terminate(true);
				MessageSender.sendMessage(player, Message.START_TIMER_MODE);
				timeTicks(endPosition, endPosState, player.getWorld().getGameTime());
			},
			0, 1
		));
	}

	@Override
	public Message noActiveTask() {
		return Message.NO_ACTIVE_TIMING;
	}

	@Override
	public Message stopMode() {
		return Message.STOP_TIMER_MODE;
	}

	@Override
	public Message currentlyExecuting() {
		return Message.CURRENTLY_TIMING;
	}

	@Override
	public Message alreadyExecuting() {
		return Message.ALREADY_TIMING;
	}

	@Override
	public Message overriden() {
		return Message.TIMER_OVERRIDEN;
	}
}
