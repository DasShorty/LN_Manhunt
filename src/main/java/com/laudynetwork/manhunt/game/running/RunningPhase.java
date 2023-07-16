package com.laudynetwork.manhunt.game.running;

import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.gameengine.game.phase.GamePhase;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.running.animation.RunningBarAnimation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RunningPhase implements GamePhase {

    private RunningBarAnimation barAnimation;
    private final ManhuntGame game;

    @Override
    public GameState state() {
        return GameState.RUNNING;
    }

    @Override
    public void onStart() {
        game.setCurrentState(GameState.RUNNING);
        barAnimation = new RunningBarAnimation(game);

        game.getAnimationController().getActionBarAnimations().add(barAnimation);
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
