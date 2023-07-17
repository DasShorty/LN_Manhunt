package com.laudynetwork.manhunt.game.running.animation;

import com.laudynetwork.gameengine.api.animation.impl.ActionBarAnimation;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.running.RunningTimer;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RunningBarAnimation extends ActionBarAnimation {
    private final ManhuntGame game;
    private final RunningTimer timer;

    public RunningBarAnimation(ManhuntGame game, RunningTimer timer) {
        super(0, 20);
        this.game = game;
        this.timer = timer;
    }

    @Override
    public Component onRender(Player player) {

        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        val time = game.getMsgApi().getTranslation(language, "actionbar.running.timer", Placeholder.unparsed("hours", timer.getHours()),
                Placeholder.unparsed("minutes", timer.getMinutes()),
                Placeholder.unparsed("seconds", timer.getSeconds()));

        return game.isManNear(player) ?
                game.getMsgApi().getTranslation(language, "actionbar.running", Placeholder.component("timer", time), Placeholder.unparsed("symbol", "(❤)"),
                        Placeholder.unparsed("huntercount", String.valueOf(game.getTeamHandler().getHuntersCount()))) :
                game.getMsgApi().getTranslation(language, "actionbar.running", Placeholder.component("timer", time), Placeholder.unparsed("symbol", ""),
                        Placeholder.unparsed("huntercount", String.valueOf(game.getTeamHandler().getHuntersCount())));
    }

    @Override
    public List<? extends Player> sendTo() {
        return Bukkit.getOnlinePlayers().stream().toList();
    }

    @Override
    public void onTick() {

    }
}
