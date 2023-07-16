package com.laudynetwork.manhunt.game.starting.animation;

import com.laudynetwork.gameengine.api.animation.impl.TitleAnimation;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.ManhuntGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class StartingTitleAnimation extends TitleAnimation {

    private final AtomicInteger counter = new AtomicInteger(4);
    private final ManhuntGame game;

    public StartingTitleAnimation(ManhuntGame game) {
        super(0, 20);
        this.game = game;
    }

    @Override
    public Title.Times times() {
        return Title.Times.times(Duration.ofSeconds(0L), Duration.ofSeconds(1L), Duration.ofSeconds(2L));
    }

    @Override
    public Component title() {
        return Component.text(counter.get());
    }

    @Override
    public Component subTitle() {
        return Component.empty();
    }

    @Override
    public List<? extends Player> sendTo() {
        return Bukkit.getOnlinePlayers().stream().toList();
    }

    @Override
    public void onTick() {
        if (counter.get() == 1) {
            cancel();
            game.loadPhase(GameState.RUNNING);
        }

        counter.decrementAndGet();
    }
}
