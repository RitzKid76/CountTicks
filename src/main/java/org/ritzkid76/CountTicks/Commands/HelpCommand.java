package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public class HelpCommand extends Command {
	public HelpCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(args.length == 0) {
			MessageSender.sendHelpMessage(player, syntaxHandler.getOptionsRoot(), label);
			return true;
		}

		return false;
	}

	@Override
	public void verifiedExecute() {
		String messageName = ("desc_" + args[0]).toUpperCase();
		MessageSender.sendMessage(player, Message.valueOf(messageName), label);
	}
}
