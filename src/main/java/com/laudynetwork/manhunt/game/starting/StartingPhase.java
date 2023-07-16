package com.laudynetwork.manhunt.game.starting;

import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.gameengine.game.phase.GamePhase;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.starting.animation.StartingBarAnimation;
import com.laudynetwork.manhunt.game.starting.animation.StartingTitleAnimation;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class StartingPhase implements GamePhase {
    private final ManhuntGame game;

    private StartingBarAnimation barAnimation;

    @Override
    public GameState state() {
        return GameState.STARTING;
    }

    @Override
    public void onStart() {
        game.setCurrentState(GameState.STARTING);

        game.getWaitingItemHandler().clearItemsFromAllPlayers();


        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGlowing(false);
            player.sendMessage(Component.text("Das Spiel startet nun!"));
        });

        var titleAnimation = new StartingTitleAnimation(game);
        barAnimation = new StartingBarAnimation();

        game.getAnimationController().getTitleAnimations().add(titleAnimation);
        game.getAnimationController().getActionBarAnimations().add(barAnimation);

    }

    @Override
    public void onStop() {
        barAnimation.cancel();
    }

    @Override
    public boolean requirement() {
        return Bukkit.getOnlinePlayers().size() >= 2;
    }
}
