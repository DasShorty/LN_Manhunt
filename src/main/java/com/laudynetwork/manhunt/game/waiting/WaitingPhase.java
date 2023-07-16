package com.laudynetwork.manhunt.game.waiting;

import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.gameengine.game.phase.GamePhase;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.waiting.animation.WaitingBarAnimation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.GameMode;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class WaitingPhase implements GamePhase {

    private final ManhuntGame game;
    private WaitingBarAnimation barAnimation;

    @Override
    public GameState state() {
        return GameState.WAITING;
    }

    @Override
    public void onStart() {

        barAnimation = new WaitingBarAnimation();
        game.getAnimationController().getActionBarAnimations().add(barAnimation);

        GameListeners.listen(BlockBreakEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.WAITING || game.getCurrentState() == GameState.STARTING)
                event.setCancelled(true);

        });

        GameListeners.listen(BlockPlaceEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.WAITING || game.getCurrentState() == GameState.STARTING)
                event.setCancelled(true);

        });

        GameListeners.listen(FoodLevelChangeEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.WAITING || game.getCurrentState() == GameState.STARTING)
                event.setCancelled(true);

        });

        GameListeners.listen(EntityDamageEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.WAITING || game.getCurrentState() == GameState.STARTING)
                event.setCancelled(true);

        });

        GameListeners.listen(PlayerMoveEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() != GameState.WAITING && game.getCurrentState() != GameState.STARTING)
                return;

            val player = event.getPlayer();
            val dataContainer = player.getPersistentDataContainer();
            if (!dataContainer.has(game.getTeamHandler().getGameRoleKey())) {
                event.setCancelled(true);
                return;
            }

            val role = dataContainer.get(game.getTeamHandler().getGameRoleKey(), PersistentDataType.STRING);

            val from = event.getFrom();
            val to = event.getTo();
            assert role != null;

            if (player.getGameMode() == GameMode.CREATIVE)
                return;

            if ((from.getX() != to.getX() || from.getZ() != from.getZ()) && role.equalsIgnoreCase("hunters"))
                event.setCancelled(true);

        });

    }

    @Override
    public void onStop() {
        barAnimation.cancel();
    }

    @Override
    public boolean requirement() {
        return true;
    }
}
