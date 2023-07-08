package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.game.Game;
import com.laudynetwork.gameengine.game.GameType;
import com.laudynetwork.gameengine.game.gamestate.GameState;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ManhuntGame extends Game {

    @Getter
    private final TeamHandler teamHandler;
    private final MongoDatabase database = Bukkit.getServicesManager().getRegistration(MongoDatabase.class).getProvider();
    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);

    public ManhuntGame() {
        super(new GameType("MANHUNT"), 110, 1);
        this.teamHandler = new TeamHandler();
    }

    @Override
    public void onGameStateChange(GameState oldState, GameState newState) {

    }

    @Override
    public boolean onLoad() {


        return true;
    }

    @Override
    public boolean onStart() {

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

        val language = new NetworkPlayer(this.database, player.getUniqueId()).getLanguage();

        event.joinMessage(Component.empty());
        broadCast("game.join", language, Placeholder.unparsed("player", player.getName()));

        if (player.hasPermission("manhunt.man") && this.teamHandler.isManTeamEmpty()) {
            this.teamHandler.playerTeam("man", player);
            player.sendMessage(this.msgApi.getMessage(language, "role.man"));
        } else {
            this.teamHandler.playerTeam("hunters", player);
            player.sendMessage(this.msgApi.getMessage(language, "role.hunter"));
        }

    }

    @Override
    public void onQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();
        val language = new NetworkPlayer(this.database, player.getUniqueId()).getLanguage();
        event.quitMessage(Component.empty());
        broadCast("game.left", language, Placeholder.unparsed("player", player.getName()));
    }
}
