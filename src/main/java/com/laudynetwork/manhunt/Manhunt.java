package com.laudynetwork.manhunt;

import com.laudynetwork.gameengine.game.backend.GameDataHandler;
import com.laudynetwork.manhunt.game.waiting.items.WaitingItemHandler;
import com.laudynetwork.networkutils.api.gui.GUIHandler;
import com.laudynetwork.networkutils.api.messanger.backend.MessageCache;
import com.laudynetwork.networkutils.api.tablist.TablistManager;
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
    private GUIHandler<?> guiHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.msgCache = new MessageCache();
        this.msgCache.loadFileInCache(this.getResource("translations/own/de.json"), "de");
        this.msgCache.loadFileInCache(this.getResource("translations/own/en.json"), "en");
        this.msgCache.loadFileInCache(this.getResource("translations/plugins/de.json"), "de");
        this.msgCache.loadFileInCache(this.getResource("translations/plugins/en.json"), "en");

        this.guiHandler = Bukkit.getServicesManager().getRegistration(GUIHandler.class).getProvider();

        val dataHandler = Bukkit.getServicesManager().getRegistration(GameDataHandler.class).getProvider();

        val tablistManager = Bukkit.getServicesManager().getRegistration(TablistManager.class).getProvider();

        tablistManager.setTablist((scoreboard, player) -> {

        });

        game = new ManhuntGame();

        val pm = Bukkit.getPluginManager();

        val itemHandler = new WaitingItemHandler(game);
        pm.registerEvents(itemHandler, this);
        this.game.onLoad();
    }

    @Override
    public void onDisable() {
        if (this.game != null)
            this.game.onStop();
    }
}
