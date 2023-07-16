package com.laudynetwork.manhunt.game.running;

import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.gameengine.game.phase.GamePhase;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.running.animation.RunningBarAnimation;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public class RunningPhase implements GamePhase {

    private final ManhuntGame game;
    private RunningBarAnimation barAnimation;

    @Override
    public GameState state() {
        return GameState.RUNNING;
    }

    @Override
    public void onStart() {
        game.setCurrentState(GameState.RUNNING);
        barAnimation = new RunningBarAnimation(game);

        game.getAnimationController().getActionBarAnimations().add(barAnimation);

        GameListeners.listen(EntityDeathEvent.class, EventPriority.HIGHEST, false, event -> {
            if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
                return;

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                val language = new NetworkPlayer(game.getDatabase(), onlinePlayer.getUniqueId()).getLanguage();
                onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.finished.man"));
            });

            game.loadPhase(GameState.ENDING);
        });

        GameListeners.listen(PlayerDeathEvent.class, EventPriority.HIGHEST, false, event -> {

            val player = event.getPlayer();

            if (game.getTeamHandler().isPlayerHunter(player.getUniqueId()))
                return;

            System.out.println(5);

            game.getTeamHandler().removeMan(player.getUniqueId());

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {

                val language = new NetworkPlayer(game.getDatabase(), onlinePlayer.getUniqueId()).getLanguage();

                onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.man.killed", Placeholder.unparsed("man", player.getName())));

                if (game.getTeamHandler().isManTeamEmpty())
                    onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.finished.mans.dead"));


            });

            if (game.getTeamHandler().isManTeamEmpty()) {
                game.loadPhase(GameState.ENDING);
            }


        });

        GameListeners.listen(PlayerDeathEvent.class, EventPriority.HIGHEST, false, playerDeathEvent -> {
            playerDeathEvent.deathMessage(Component.empty());
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
