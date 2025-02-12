package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.BuildScanner;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class CountCommand extends ThreadCommand {
	private BlockVector3 startPosition;
	private BlockVector3 endPosition;

	public CountCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if (playerData.executionCancelCheck(label))
			return true;

		startPosition = playerData.getFirstPosition();
		if (startPosition == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			endedTask(true);
			return true;
		}
		endPosition = playerData.getSecondPosition();
		if (endPosition == null) {
			MessageSender.sendMessage(player, Message.NO_END_SELECTED);
			endedTask(true);
			return true;
		}
		
		if(playerData.getRegion() == null) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION, label);
			endedTask(true);
			return true;
		}
		
		if (!playerData.getRegion().contains(endPosition)) {
			MessageSender.sendMessage(player, Message.END_OUT_OF_BOUNDS);
			endedTask(true);
			return true;
		}

		return false;
	}

	BlockVector3 callbackEndpoint;
	private void callback(boolean success) {
		endedTask(true);

		if (!success)
			return;

		ArgumentParser.sendInspectorMessage(player, playerData.getGraph().findFastestPath(callbackEndpoint));
	}

	@Override
	public void verifiedExecute() {
		callbackEndpoint = endPosition;
		if (scanValidation(startPosition))
			return;
		
		callback(true);
	}

	private boolean runScan(BlockVector3 origin) {
		BuildScanner scanner = new BuildScanner(playerData, this::callback);
		if (!scanner.trySetOrigin(origin, label))
			return false;

		startedTask(scanner.scan());

		return true;
	}

	private boolean scanValidation(BlockVector3 origin) {
		if(!playerData.hasScanned())
			return runScan(origin);

		if(playerData.getGraph().getOrigin() != origin) {
			MessageSender.sendMessage(player, Message.START_CHANGED);
			return runScan(origin);
		}

		if(playerData.getGraph().getRegion() != playerData.getRegion()) {
			MessageSender.sendMessage(player, Message.REGION_CHANGED);
			playerData.updateRegion(label);
			return runScan(origin);
		}

		return false;
	}

	@Override
	public Message noActiveTask() {
		return Message.SHOULD_NOT_HAPPEN;
	}
	
	@Override
	public Message stopMode() {
		return Message.SHOULD_NOT_HAPPEN;
	}
	
	@Override
	public Message currentlyExecuting() {
		return Message.SHOULD_NOT_HAPPEN;
	}
	
	@Override
	public Message alreadyExecuting() {
		return Message.SHOULD_NOT_HAPPEN;
	}
	
	@Override
	public Message overriden() {
		return Message.SHOULD_NOT_HAPPEN;
	}
}
