package com.laudynetwork.manhunt.game.running;

import com.laudynetwork.gameengine.api.endplayer.EndPlayerHandler;
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
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.time.Duration;

@RequiredArgsConstructor
public class RunningPhase implements GamePhase {

    private final ManhuntGame game;
    private RunningBarAnimation barAnimation;
    private RunningTimer timer;
    private EndPlayerHandler endPlayerHandler = Bukkit.getServicesManager().getRegistration(EndPlayerHandler.class).getProvider();

    @Override
    public GameState state() {
        return GameState.RUNNING;
    }

    @Override
    public void onStart() {
        game.setCurrentState(GameState.RUNNING);

        timer = new RunningTimer();

        barAnimation = new RunningBarAnimation(game, timer);

        game.getAnimationController().getActionBarAnimations().add(barAnimation);

        GameListeners.listen(EntityDeathEvent.class, EventPriority.HIGHEST, false, event -> {
            if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
                return;

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                val language = new NetworkPlayer(game.getDatabase(), onlinePlayer.getUniqueId()).getLanguage();
                onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.finished.man"));
                onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(5)));
                onlinePlayer.sendTitlePart(TitlePart.TITLE, game.getMsgApi().getTranslation(language, "game.finished.title"));
            });

            game.loadPhase(GameState.ENDING);
        });

        GameListeners.listen(PlayerDeathEvent.class, EventPriority.HIGHEST, false, event -> {

            val player = event.getPlayer();

            if (game.getTeamHandler().isPlayerHunter(player.getUniqueId()))
                return;

            game.getTeamHandler().removeMan(player.getUniqueId());

            val location = player.getLocation();

            endPlayerHandler.handle(player);

            player.teleportAsync(location);

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {

                val language = new NetworkPlayer(game.getDatabase(), onlinePlayer.getUniqueId()).getLanguage();

                onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.man.killed", Placeholder.unparsed("man", player.getName())));

                if (game.getTeamHandler().isManTeamEmpty()) {
                    onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(5)));
                    onlinePlayer.sendTitlePart(TitlePart.TITLE, game.getMsgApi().getTranslation(language, "game.finished.title"));
                    onlinePlayer.sendTitlePart(TitlePart.SUBTITLE, game.getMsgApi().getTranslation(language, "game.finished.time", Placeholder.unparsed("hours", timer.getHours()),
                            Placeholder.unparsed("minutes", timer.getMinutes()),
                            Placeholder.unparsed("seconds", timer.getSeconds())));
                    onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.finished.time", Placeholder.unparsed("hours", timer.getHours()),
                            Placeholder.unparsed("minutes", timer.getMinutes()),
                            Placeholder.unparsed("seconds", timer.getSeconds())));
                    onlinePlayer.sendMessage(game.getMsgApi().getMessage(language, "game.finished.mans.dead"));
                }

            });

            if (game.getTeamHandler().isManTeamEmpty())
                game.loadPhase(GameState.ENDING);


        });

        GameListeners.listen(PlayerDeathEvent.class, EventPriority.HIGHEST, false, playerDeathEvent -> {
            playerDeathEvent.deathMessage(Component.empty());
        });

        GameListeners.listen(InventoryClickEvent.class, EventPriority.HIGHEST, false, event -> {
            if (game.getCurrentState() == GameState.RUNNING)
                event.setCancelled(false);
        });

        GameListeners.listen(InventoryDragEvent.class, EventPriority.HIGHEST, false, event -> {
            if (game.getCurrentState() == GameState.RUNNING)
                event.setCancelled(false);
        });
    }

    @Override
    public void onStop() {
        barAnimation.cancel();
        timer.getTask().cancel();
    }

    @Override
    public boolean requirement() {
        return true;
    }
}
