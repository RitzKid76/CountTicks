package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
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
			playerData.terminatePulse();
			return true;
		}

		position = playerData.getFirstPosition();
		if(position == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}

		return false;
	}

	@Override
	public void verifiedExecute() {
		playerData.pulse(position, label);
	}
}
