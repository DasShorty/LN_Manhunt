package com.laudynetwork.manhunt.game.waiting.items;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import org.bukkit.event.player.PlayerInteractEvent;

public interface WaitingItem {

    int slot();
    ItemStackBuilder<?> item();

    void onClick(PlayerInteractEvent event);

}
