package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.game.backend.GameDataHandler;
import com.laudynetwork.manhunt.game.WaitingListeners;
import com.laudynetwork.networkutils.api.messanger.backend.MessageCache;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Manhunt extends JavaPlugin {
    @Getter
    private static Manhunt INSTANCE;
    private ManhuntGame game;
    private MessageCache msgCache;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.msgCache = new MessageCache();
        this.msgCache.loadFileInCache(this.getResource("translations/own/de.json"), "de");
        this.msgCache.loadFileInCache(this.getResource("translations/own/en.json"), "en");
        this.msgCache.loadFileInCache(this.getResource("translations/plugins/de.json"), "de");
        this.msgCache.loadFileInCache(this.getResource("translations/plugins/en.json"), "en");

        val dataHandler = Bukkit.getServicesManager().getRegistration(GameDataHandler.class).getProvider();
        game = new ManhuntGame();

        val pm = Bukkit.getPluginManager();
        pm.registerEvents(this.game, this);
        pm.registerEvents(new WaitingListeners(this.game), this);
        this.game.onLoad();
    }

    @Override
    public void onDisable() {
        if (this.game != null)
            this.game.onStop();
    }
}
