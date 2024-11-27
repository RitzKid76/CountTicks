package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
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
			playerData.terminateTimer();
			return true;
		}
		
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

	@Override
	public void verifiedExecute() {
		playerData.timer(startPosition, endPosition, label);
	}
}
