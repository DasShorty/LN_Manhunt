package com.laudynetwork.manhunt.game.waiting;

import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.ManhuntGame;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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

        val from = event.getFrom();
        val to = event.getTo();
        assert role != null;

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        if ((from.getX() != to.getX() || from.getZ() != from.getZ()) && role.equalsIgnoreCase("hunters"))
            event.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (isNotWaiting())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        event.setCancelled(true);
    }
}
