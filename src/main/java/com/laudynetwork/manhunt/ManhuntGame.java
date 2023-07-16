package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.api.animation.AnimationController;
import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.Game;
import com.laudynetwork.gameengine.game.GameType;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.game.running.RunningPhase;
import com.laudynetwork.manhunt.game.starting.StartingPhase;
import com.laudynetwork.manhunt.game.waiting.WaitingPhase;
import com.laudynetwork.manhunt.game.waiting.items.WaitingItemHandler;
import com.laudynetwork.manhunt.game.waiting.items.impl.MenuItem;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class ManhuntGame extends Game {


    private final TeamHandler teamHandler;
    private final WaitingItemHandler waitingItemHandler;
    private final MongoDatabase database = Bukkit.getServicesManager().getRegistration(MongoDatabase.class).getProvider();
    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);
    private final AnimationController animationController;

    public ManhuntGame() {
        super(new GameType("MANHUNT"), 110, 1);
        this.teamHandler = new TeamHandler();
        this.waitingItemHandler = new WaitingItemHandler(this);
        this.animationController = Bukkit.getServicesManager().getRegistration(AnimationController.class).getProvider();

    }

    @Override
    public void onGameStateChange(GameState oldState, GameState newState) {

    }

    @Override
    public boolean onLoad() {
        registerPhase(new WaitingPhase(this));
        registerPhase(new StartingPhase(this));
        registerPhase(new RunningPhase(this));

        loadPhase(GameState.WAITING);

        GameListeners.listen(PlayerJoinEvent.class, EventPriority.NORMAL, true, event -> {
            val player = event.getPlayer();

            player.getInventory().clear();

            val language = new NetworkPlayer(this.database, player.getUniqueId()).getLanguage();

            event.joinMessage(Component.empty());
            broadCast("game.join", language, Placeholder.unparsed("player", player.getName()));

            if (player.hasPermission("manhunt.man") && this.teamHandler.isManTeamEmpty()) {
                this.teamHandler.playerTeam("man", player, this.gameTeamHandler);
                player.sendMessage(this.msgApi.getMessage(language, "role.man"));
            } else {
                this.teamHandler.playerTeam("hunters", player, this.gameTeamHandler);
                player.sendMessage(this.msgApi.getMessage(language, "role.hunter"));
            }

            addHotBarItems(player);
        });

        GameListeners.listen(PlayerQuitEvent.class, EventPriority.NORMAL, true, event -> {
            val player = event.getPlayer();
            val language = new NetworkPlayer(this.database, player.getUniqueId()).getLanguage();
            event.quitMessage(Component.empty());
            broadCast("game.left", language, Placeholder.unparsed("player", player.getName()));
        });
        return true;
    }

    public boolean isManNear(Player player) {

        CompletableFuture<Boolean> bool = new CompletableFuture<>();

        val list = Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> teamHandler.isPlayerMan(onlinePlayer.getUniqueId())).toList();
        list.forEach(man -> {
            if (man.equals(player)) {
                bool.complete(false);
                return;
            }
            if (man.getLocation().distance(player.getLocation()) < 50) {
                bool.complete(true);
            }
        });

        return bool.getNow(false);
    }

    @Override
    public boolean onStop() {

        return true;
    }

    private void broadCast(String key, String language, TagResolver... resolvers) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(this.msgApi.getTranslation(language, key, resolvers)));
    }

    private void addHotBarItems(Player player) {
        this.waitingItemHandler.addItemToPlayer(player, new MenuItem(this.teamHandler, this.gameTeamHandler, this));

        this.waitingItemHandler.reloadPlayerItems(player);
    }
}
