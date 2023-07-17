package com.laudynetwork.manhunt.game.waiting.items;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface WaitingItem {

    int slot();
    ItemStackBuilder<?> item(Player player);

    void onClick(PlayerInteractEvent event);

}
