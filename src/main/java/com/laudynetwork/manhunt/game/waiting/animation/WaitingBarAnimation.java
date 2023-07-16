package com.laudynetwork.manhunt.game.waiting.animation;

import com.laudynetwork.gameengine.api.animation.impl.ActionBarAnimation;
import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class WaitingBarAnimation extends ActionBarAnimation {

    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);
    private final MongoDatabase database = Bukkit.getServicesManager().getRegistration(MongoDatabase.class).getProvider();

    public WaitingBarAnimation() {
        super(20, 3*20);
    }

    @Override
    public Component onRender(Player player) {
        val language = new NetworkPlayer(database, player.getUniqueId()).getLanguage();
        return msgApi.getTranslation(language, "actionbar.waiting");
    }

    @Override
    public List<? extends Player> sendTo() {
        return Bukkit.getOnlinePlayers().stream().toList();
    }

    @Override
    public void onTick() {

    }
}
