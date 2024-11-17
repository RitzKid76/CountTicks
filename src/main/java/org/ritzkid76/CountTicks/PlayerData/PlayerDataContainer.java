package org.ritzkid76.CountTicks.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerDataContainer {
	private final Map<UUID, PlayerData> players = new HashMap<>();

	public PlayerData get(Player player) {
		UUID uuid = player.getUniqueId();
		return get(uuid);
	}
	public PlayerData get(UUID uuid) {
		return players.computeIfAbsent(uuid, PlayerData::new);
	}

	public void shutdown() {
		for(PlayerData playerData : players.values()) {
			playerData.shutdown();
		}
	}

	public void shutdown(UUID uuid) {
		get(uuid).shutdown();
	}
}
