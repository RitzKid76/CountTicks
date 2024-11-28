package org.ritzkid76.CountTicks.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerDataDirectory {
	private final Map<UUID, PlayerData> players = new HashMap<>();
	private final Plugin plugin;

	public PlayerDataDirectory(Plugin p) {
		plugin = p;
	}

	public PlayerData getOrCreate(Player player) {
		UUID uuid = player.getUniqueId();
		return getOrCreate(uuid);
	}
	public PlayerData getOrCreate(UUID uuid) {
		return players.computeIfAbsent(uuid, u -> new PlayerData(u, plugin));
	}
	public PlayerData get(UUID uuid) {
		return players.get(uuid);
	}

	public void shutdown() {
		for(PlayerData playerData : players.values()) {
			playerData.shutdown();
		}
	}

	public void shutdown(UUID uuid) {
		PlayerData player = get(uuid);
		if(player != null)
			player.shutdown();
	}
}
