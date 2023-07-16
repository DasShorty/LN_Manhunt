package com.laudynetwork.manhunt.game.ending;

import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.gameengine.game.phase.GamePhase;
import com.laudynetwork.manhunt.ManhuntGame;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@RequiredArgsConstructor
public class EndingPhase implements GamePhase {

    private final ManhuntGame game;

    @Override
    public GameState state() {
        return GameState.ENDING;
    }

    @Override
    public void onStart() {
        game.setCurrentState(GameState.ENDING);
        GameListeners.listen(BlockBreakEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.ENDING)
                event.setCancelled(true);

        });

        GameListeners.listen(BlockPlaceEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.ENDING)
                event.setCancelled(true);

        });

        GameListeners.listen(FoodLevelChangeEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.ENDING)
                event.setCancelled(true);

        });

        GameListeners.listen(EntityDamageEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (game.getCurrentState() == GameState.ENDING)
                event.setCancelled(true);

        });
    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean requirement() {
        return true;
    }
}
