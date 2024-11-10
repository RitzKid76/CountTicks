package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
	PlayerDataContainer playerDataContainer;

	public PlayerEventListener(PlayerDataContainer pdc) {
		playerDataContainer = pdc;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		playerDataContainer.shutdown(uuid);
	}
}
