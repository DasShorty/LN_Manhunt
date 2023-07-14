package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.api.animation.AnimationController;
import com.laudynetwork.gameengine.api.animation.impl.ActionBarAnimation;
import com.laudynetwork.gameengine.api.animation.impl.TitleAnimation;
import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.Game;
import com.laudynetwork.gameengine.game.GameType;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.game.waiting.items.WaitingItemHandler;
import com.laudynetwork.manhunt.game.waiting.items.impl.MenuItem;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManhuntGame extends Game {

    @Getter
    private final TeamHandler teamHandler;
    private final WaitingItemHandler waitingItemHandler;
    private final MongoDatabase database = Bukkit.getServicesManager().getRegistration(MongoDatabase.class).getProvider();
    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);
    private final AnimationController animationController;

    public ManhuntGame() {
        super(new GameType("MANHUNT"), 110, 1);
        this.teamHandler = new TeamHandler();
        this.waitingItemHandler = new WaitingItemHandler();
        Bukkit.getPluginManager().registerEvents(this.waitingItemHandler, Manhunt.getINSTANCE());
        this.animationController = Bukkit.getServicesManager().getRegistration(AnimationController.class).getProvider();
    }

    @Override
    public void onGameStateChange(GameState oldState, GameState newState) {

    }

    @Override
    public boolean onLoad() {

        GameListeners.listen(BlockBreakEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (getCurrentState() == GameState.WAITING)
                event.setCancelled(true);

        });

        GameListeners.listen(BlockPlaceEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (getCurrentState() == GameState.WAITING)
                event.setCancelled(true);

        });

        GameListeners.listen(FoodLevelChangeEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (getCurrentState() == GameState.WAITING)
                event.setCancelled(true);

        });

        GameListeners.listen(EntityDamageEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (getCurrentState() == GameState.WAITING)
                event.setCancelled(true);

        });

        GameListeners.listen(PlayerMoveEvent.class, EventPriority.NORMAL, false, (event) -> {

            if (getCurrentState() != GameState.WAITING)
                return;

            val player = event.getPlayer();
            val dataContainer = player.getPersistentDataContainer();
            if (!dataContainer.has(this.getTeamHandler().getGameRoleKey())) {
                event.setCancelled(true);
                return;
            }

            val role = dataContainer.get(this.getTeamHandler().getGameRoleKey(), PersistentDataType.STRING);

            val from = event.getFrom();
            val to = event.getTo();
            assert role != null;

            if (player.getGameMode() == GameMode.CREATIVE)
                return;

            if ((from.getX() != to.getX() || from.getZ() != from.getZ()) && role.equalsIgnoreCase("hunters"))
                event.setCancelled(true);

        });

        animationController.getActionBarAnimations().add(new ActionBarAnimation(0, 40) {
            @Override
            public Component onRender() {


                switch (getCurrentState()) {
                    case WAITING -> {
                        return Component.text("Manhunt on laudynetwork.com").color(NamedTextColor.GOLD);
                    }
                    case STARTING -> {
                        return Component.text("Manhunt is starting....").color(NamedTextColor.GRAY);
                    }
                    case ENDING -> {
                        return Component.text("Thanks for playing Manhunt on laudynetwork.com. See you soon!").color(NamedTextColor.GREEN);
                    }
                    case RUNNING -> {
                        return Component.text("20:35 | 19 Hunter (♡)").color(NamedTextColor.BLUE);
                    }
                }

                return Component.text("Error || something went wrong!").color(NamedTextColor.RED);
            }

            @Override
            public List<? extends Player> sendTo() {
                return Bukkit.getOnlinePlayers().stream().toList();
            }
        });
        return true;
    }

    @Override
    public boolean onStart() {

        if (Bukkit.getOnlinePlayers().size() < 2)
            return false;

        setCurrentState(GameState.STARTING);

        val counter = new AtomicInteger(4);
        final int[] current = {counter.get()};
        animationController.getTitleAnimations().add(new TitleAnimation(20, 20) {
            @Override
            public Title.Times times() {
                return Title.Times.times(Duration.ofSeconds(0L),Duration.ofSeconds(3L),Duration.ofSeconds(2L));
            }

            @Override
            public Component title() {
                return Component.text(current[0]);
            }

            @Override
            public Component subTitle() {
                return Component.empty();
            }

            @Override
            public List<? extends Player> sendTo() {

                if (current[0] == 1) {
                    cancel();
                    setCurrentState(GameState.RUNNING);
                }

                current[0] = counter.decrementAndGet();

                return Bukkit.getOnlinePlayers().stream().toList();
            }
        });

        Bukkit.getOnlinePlayers().forEach(player ->  {
            player.setGlowing(false);
            player.sendMessage(Component.text("Das Spiel startet nun!"));

        });

        return true;
    }

    @Override
    public boolean onStop() {

        return true;
    }

    private void broadCast(String key, String language, TagResolver... resolvers) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(this.msgApi.getTranslation(language, key, resolvers)));
    }

    @Override
    public void onJoin(PlayerJoinEvent event) {

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
    }

    private void addHotBarItems(Player player) {
        this.waitingItemHandler.addItemToPlayer(player, new MenuItem(this.teamHandler, this.gameTeamHandler, this));

        this.waitingItemHandler.reloadPlayerItems(player);
    }

    @Override
    public void onQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();
        val language = new NetworkPlayer(this.database, player.getUniqueId()).getLanguage();
        event.quitMessage(Component.empty());
        broadCast("game.left", language, Placeholder.unparsed("player", player.getName()));
    }
}
