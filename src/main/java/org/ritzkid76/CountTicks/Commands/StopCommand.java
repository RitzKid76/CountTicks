package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public class StopCommand extends Command {
	public StopCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public void verifiedExecute() {
		playerData.shutdown();
		MessageSender.sendMessage(player, Message.STOPPED_ALL);
	}
}
