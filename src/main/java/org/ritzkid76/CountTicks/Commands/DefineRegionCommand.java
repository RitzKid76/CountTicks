package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class DefineRegionCommand extends Command {
	private Region region;

	public DefineRegionCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
			return true;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return true;
		}

		region = playerData.updateRegion(label);
		if(region == null)
			return true;

		return false;
	}

	@Override
	public void verifiedExecute() {
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		MessageSender.sendMessage(
			player, Message.SET_SCAN_REGION,
			String.valueOf(min.x()), String.valueOf(min.y()), String.valueOf(min.z()),
			String.valueOf(max.x()), String.valueOf(max.y()), String.valueOf(max.z())
		);
	}
}
