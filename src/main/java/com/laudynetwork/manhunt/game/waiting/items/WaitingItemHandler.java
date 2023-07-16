package com.laudynetwork.manhunt.game.waiting.items;

import com.laudynetwork.gameengine.api.listener.GameListeners;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.ManhuntGame;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class WaitingItemHandler implements Listener {

    private final Map<UUID, List<WaitingItem>> hotBarItems = new HashMap<>();
    private final ManhuntGame game;

    public WaitingItemHandler(ManhuntGame game) {
        this.game = game;

        GameListeners.listen(PlayerInteractEvent.class, EventPriority.NORMAL, true, event -> {
            val player = event.getPlayer();

            if (this.game.getCurrentState() != GameState.WAITING)
                return;

            if (!this.hotBarItems.containsKey(player.getUniqueId()))
                return;

            val waitingItems = this.hotBarItems.get(player.getUniqueId());

            val list = waitingItems.stream().filter(waitingItem -> waitingItem.slot() == player.getInventory().getHeldItemSlot()).toList();

            if (list.isEmpty())
                return;

            val item = list.get(0);

            event.setCancelled(true);
            item.onClick(event);
        });
    }

    public void addItemToPlayer(Player player, WaitingItem item) {

        if (!this.hotBarItems.containsKey(player.getUniqueId())) {
            this.hotBarItems.put(player.getUniqueId(), List.of(item));
            return;
        }

        val items = new ArrayList<>(this.hotBarItems.get(player.getUniqueId()));
        items.add(item);
        this.hotBarItems.put(player.getUniqueId(), items);
    }

    public void loadAllItems() {
        hotBarItems.forEach((uuid, waitingItems) -> {

            val player = Bukkit.getPlayer(uuid);
            assert player != null;

            waitingItems.forEach(waitingItem -> {
                player.getInventory().setItem(waitingItem.slot(), waitingItem.item().build());
            });

        });
    }

    public void reloadPlayerItems(Player player) {
        if (!hotBarItems.containsKey(player.getUniqueId()))
            return;
        val waitingItems = hotBarItems.get(player.getUniqueId());
        waitingItems.forEach(waitingItem -> {
            player.getInventory().setItem(waitingItem.slot(), waitingItem.item().build());
        });
    }

    public void clearItemsFromAllPlayers() {
        hotBarItems.forEach((uuid, waitingItems) -> {

            val player = Bukkit.getPlayer(uuid);
            assert player != null;

            waitingItems.forEach(waitingItem -> {
                player.getInventory().clear(waitingItem.slot());
            });

        });
    }

}
