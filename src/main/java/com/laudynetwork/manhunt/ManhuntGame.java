package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.game.Game;
import com.laudynetwork.gameengine.game.GameType;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.team.TeamData;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ManhuntGame extends Game {

    private final TeamData hunters;
    private final TeamData man;
    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);

    public ManhuntGame() {
        super(new GameType("MANHUNT"), 110, 1);

        this.hunters = new TeamData("hunters", Component.empty(), Component.empty(), NamedTextColor.RED);
        this.man = new TeamData("man", Component.empty(), Component.empty(), NamedTextColor.GREEN);

    }

    @Override
    public void onGameStateChange(GameState oldState, GameState newState) {

    }

    public void playerTeam(String id, Player player) {

        switch (id) {
            case "hunters" -> this.teamHandler.team(id, player.getScoreboard(), this.hunters.prefix(), this.hunters.suffix(), this.hunters.color());
            case "man" -> this.teamHandler.team(id, player.getScoreboard(), this.man.prefix(), this.man.suffix(), this.man.color());
            default -> {
                // ignore
            }
        }

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

    @Override
    public void onJoin(PlayerJoinEvent playerJoinEvent) {

    }

    @Override
    public void onQuit(PlayerQuitEvent playerQuitEvent) {

    }
}
