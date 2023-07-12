package com.laudynetwork.manhunt.game.waiting.items.impl;

import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.manhunt.game.waiting.items.WaitingItem;
import com.laudynetwork.manhunt.game.waiting.items.impl.menu.MenuUI;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
@RequiredArgsConstructor

public class MenuItem implements WaitingItem {

    private final TeamHandler teamHandler;

    @Override
    public int slot() {
        return 4;
    }

    @Override
    public ItemStackBuilder<?> item() {
        return new ItemBuilder(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
                .displayName(Component.text("Menu").color(NamedTextColor.GRAY));
    }

    @Override
    public void onClick(PlayerInteractEvent event) {
        val player = event.getPlayer();

        if (player.hasCooldown(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
            return;

        player.setCooldown(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 20 * 2);

        Manhunt.getINSTANCE().getGuiHandler().open(player, new MenuUI(player, Component.text("Menu"), this.teamHandler));


    }
}
