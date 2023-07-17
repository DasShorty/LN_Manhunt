package com.laudynetwork.manhunt.game.waiting.items.impl;

import com.laudynetwork.gameengine.game.GameTeamHandler;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.waiting.items.WaitingItem;
import com.laudynetwork.manhunt.game.waiting.items.impl.menu.MenuUI;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

@RequiredArgsConstructor

public class MenuItem implements WaitingItem {

    private final TeamHandler teamHandler;
    private final GameTeamHandler gameTeamHandler;
    private final ManhuntGame game;

    @Override
    public int slot() {
        return 4;
    }

    @Override
    public ItemStackBuilder<?> item(Player player) {
        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        return new ItemBuilder(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
                .lore(getLore(language, "game.item.lore"))
                .displayName(game.getMsgApi().getTranslation(language, "game.item.displayname"));
    }

    private ArrayList<Component> getLore(String language, String key) {

        val translation = game.getMsgApi().getRaw(language, key);
        val split = translation.rawTranslation().split(";");

        MiniMessage miniMessage = MiniMessage.miniMessage();
        ArrayList<Component> components = new ArrayList<>();

        for (String s : split) {
            components.add(miniMessage.deserialize(s).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return components;
    }

    @Override
    public void onClick(PlayerInteractEvent event) {
        val player = event.getPlayer();

        if (player.hasCooldown(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
            return;

        if (game.getCurrentState() != GameState.WAITING)
            return;

        player.setCooldown(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 20 * 2);

        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        Manhunt.getINSTANCE().getGuiHandler().open(player, new MenuUI(player, game.getMsgApi().getTranslation(language, "game.ui.game.title"), this.teamHandler, this.gameTeamHandler, this.game));
    }
}
