package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class ScanCommand extends ThreadCommand {
	private BlockVector3 origin;

	public ScanCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(args.length > 0) {
			playerData.terminateScan();
			return true;
		}

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.ALREADY_SCANNING);
			return true;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return true;
		}

		origin = playerData.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}

		return false;
	}

	@Override
	public void verifiedExecute() {
		playerData.scan(origin, label);
	}
	
}
