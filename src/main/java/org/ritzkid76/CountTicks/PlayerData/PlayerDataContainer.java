package org.ritzkid76.CountTicks.PlayerData;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataContainer {
	private Map<UUID, PlayerData> players;

	public PlayerDataContainer() { players = new HashMap<>(); }

	public PlayerData get(Player player) {
		UUID uuid = player.getUniqueId();
		players.putIfAbsent(uuid, new PlayerData(player));

		return players.get(uuid);
	}

	public void shutdown() {
		for(PlayerData playerData : players.values()) {
			playerData.shutdown();
		}
	}
}
