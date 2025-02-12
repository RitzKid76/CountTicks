package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.BuildScanner;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class ScanCommand extends ThreadCommand {
	private BlockVector3 origin;

	public ScanCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	BuildScanner scanner;
	@Override
	public boolean cancelationScreening() {		
		if(args.length > 0) {
			endedTask();
			return true;
		}

		if(playerData.executionCancelCheck(label))
			return true;

		origin = playerData.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}

		scanner = new BuildScanner(playerData, this::callback);
		if(!scanner.trySetOrigin(origin, label))
			return true;

		return false;
	}

	public void callback(boolean success) {
		endedTask(true);
	}

	@Override
	public void verifiedExecute() {
		startedTask(scanner.scan());
	}

	@Override
	public Message noActiveTask() {
		return Message.NO_ACTIVE_SCAN;
	}

	@Override
	public Message stopMode() {
		return Message.STOP_SCAN_MODE;
	}

	@Override
	public Message currentlyExecuting() {
		return Message.CURRENTLY_SCANNING;
	}

	@Override
	public Message alreadyExecuting() {
		return Message.ALREADY_SCANNING;
	}

	@Override
	public Message overriden() {
		return Message.SCAN_OVERRIDEN;
	}
}
