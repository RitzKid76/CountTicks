package org.ritzkid76.CountTicks.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerDataContainer {
	private static final Map<UUID, PlayerData> players = new HashMap<>();

	public static PlayerData get(Player player) {
		UUID uuid = player.getUniqueId();
		return get(uuid);
	}
	public static PlayerData get(UUID uuid) {
		players.putIfAbsent(uuid, new PlayerData(uuid));

		return players.get(uuid);
	}

	public static void shutdown() {
		for(PlayerData playerData : players.values()) {
			playerData.shutdown();
		}
	}

	public static void shutdown(UUID uuid) {
		get(uuid).shutdown();
	}
}
