package com.laudynetwork.manhunt.game;

import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.ManhuntGame;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class WaitingListeners implements Listener {

    private final ManhuntGame game;

    private boolean isNotWaiting() {
        return this.game.getCurrentState() != GameState.WAITING;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (isNotWaiting())
            return;

        val player = event.getPlayer();
        val dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(this.game.getTeamHandler().getGameRoleKey())) {
            event.setCancelled(true);
            return;
        }

        val role = dataContainer.get(this.game.getTeamHandler().getGameRoleKey(), PersistentDataType.STRING);

        switch (role.toLowerCase()) {
            case "hunters" -> event.setCancelled(true);
            case "man" -> event.setCancelled(false);
        }

    }
}
