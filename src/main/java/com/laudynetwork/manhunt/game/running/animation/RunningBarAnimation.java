package com.laudynetwork.manhunt.game.running.animation;

import com.laudynetwork.gameengine.api.animation.impl.ActionBarAnimation;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class RunningBarAnimation extends ActionBarAnimation {
    private final ManhuntGame game;
    private long startTime;

    public RunningBarAnimation(ManhuntGame game) {
        super(0, 20);
        this.game = game;
        startTime = System.currentTimeMillis() - Duration.ofHours(1).toMillis();
    }

    @Override
    public Component onRender(Player player) {
        val current = System.currentTimeMillis();
        val hours = new SimpleDateFormat("HH").format(new Date(current - startTime));
        val minutes = new SimpleDateFormat("mm").format(new Date(current - startTime));
        val seconds = new SimpleDateFormat("ss").format(new Date(current - startTime));

        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        val time = game.getMsgApi().getTranslation(language, "actionbar.running.timer", Placeholder.unparsed("hours", hours),
                Placeholder.unparsed("minutes", minutes),
                Placeholder.unparsed("seconds", seconds));

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
