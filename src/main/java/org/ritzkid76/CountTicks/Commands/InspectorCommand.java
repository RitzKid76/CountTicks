package org.ritzkid76.CountTicks.Commands;

import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

import com.sk89q.worldedit.math.BlockVector3;

public class InspectorCommand extends ThreadCommand {
	public InspectorCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	private void inspect() {
		MessageSender.sendMessage(player, Message.START_INSPECT_MODE);

		class BlockVector3Wrapper {
			BlockVector3 blockVector3;
		}

		AtomicBoolean canEnterSafeZone = new AtomicBoolean(true);
		BlockVector3Wrapper wrapper = new BlockVector3Wrapper();
		startedTask(Bukkit.getScheduler().runTaskTimerAsynchronously(
			plugin,
			() -> {
				if(!canEnterSafeZone.compareAndSet(true, false))
					return;

				try {
					BlockVector3 viewedBlock = BlockGetter.getBlockLookingAt(player, 10);

					if(viewedBlock == null || viewedBlock.equals(wrapper.blockVector3))
						return;
					wrapper.blockVector3 = viewedBlock;

					ArgumentParser.sendInspectorMessageSubtitle(player, playerData.getGraph().findFastestPath(viewedBlock));
				} finally {
					canEnterSafeZone.set(true);
				}
			},
			0, 1
		));
	}

	@Override
	public boolean isExecuting() {
		if(args.length > 0) {
			String arg = args[0];
			if(
				arg.equals("start") && !isRunning() ||
				arg.equals("stop")
			)
				return false;
		}

		return true;
	}
	
	@Override
	public boolean shouldLinkToCurrentThread() {
		return true;
	}

	@Override
	public boolean isAlreadyExecuting(ThreadCommand newThreadCommand) {
		String[] newArgs = newThreadCommand.getArgs();
		
		if(
			args.length == 0 ||
			newArgs.length == 0
		)
			return false;

		return this.args[0] == args[0];
	}

	public void toggleInspector() {
		if(!isRunning())
			inspect();
		else
			endedTask();
	}

	@Override
	public boolean cancelationScreening() {
		if(!playerData.hasScanned()) {
			MessageSender.sendMessage(player, Message.NO_SCANNED_BUILD, label);
			endedTask(true);
			return true;
		}
		
		if(args.length == 0) {
			toggleInspector();
			return true;
		}
		
		if(executionCancelCheck()) {
			return true;
		}

		return false;
	}

	@Override
	public void verifiedExecute() {
		switch(args[0]) {
			case "start" -> inspect();
			case "stop" -> endedTask();
		}
	}

	@Override
	public Message noActiveTask() {
		return Message.NO_ACTIVE_INSPECTION;
	}

	@Override
	public Message stopMode() {
		return Message.STOP_INSPECT_MODE;
	}

	@Override
	public Message currentlyExecuting() {
		return Message.CURRENTLY_INSPECTING;
	}

	@Override
	public Message alreadyExecuting() {
		return Message.ALREADY_INSPECTING;
	}

	@Override
	public Message overriden() {
		return Message.INSPECTOR_OVERRIDEN;
	}
}
