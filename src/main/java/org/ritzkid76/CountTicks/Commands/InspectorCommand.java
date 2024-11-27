package org.ritzkid76.CountTicks.Commands;

import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxHandler;

public class InspectorCommand extends ThreadCommand {
	public InspectorCommand(String[] args, PlayerData playerData, String label, SyntaxHandler syntaxHandler) {
		super(args, playerData, label, syntaxHandler);
	}

	@Override
	public boolean cancelationScreening() {
		if(args.length == 0) {
			playerData.toggleInspector(label);
			return true;
		}

		return false;
	}

	@Override
	public void verifiedExecute() {
		switch(args[0]) {
			case "start" -> playerData.inspect(label);
			case "stop" -> playerData.terminateInspect();
		}
	}
}
