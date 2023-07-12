package com.laudynetwork.manhunt.game.waiting.items.impl.menu.role;

import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoleUI extends GUI {

    private final Map<Integer, List<String>> pagination = partition(Bukkit.getOnlinePlayers().stream().map(player -> player.getUniqueId().toString()).toList(), 36);
    private int currentPage = 0;

    public RoleUI(Player player, Component displayName) {
        super(player, displayName, 45);
    }

    static Map<Integer, List<String>> partition(List<String> list, int pageSize) {
        return IntStream.iterate(0, i -> i + pageSize)
                .limit((list.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(Collectors.toMap(i -> i / pageSize,
                        i -> list.subList(i, Math.min(i + pageSize, list.size()))));
    }

    @Override
    public void generateGUI(Player player) {

        if (pagination.keySet().size() > 1)
            set(44, new ItemBuilder(Material.GREEN_BED)
                    .displayName(Component.text("nächste Seite")));

        if (pagination.keySet().size() != 0)
            set(36, new ItemBuilder(Material.RED_BED)
                    .displayName(Component.text("vorherige Seite")));

        if (!pagination.containsKey(currentPage))
            currentPage = 0;

        val players = pagination.get(currentPage);

        for (int i = 0; i < 35; i++) {

            if (players.size() - 1 < i)
                continue;

            val playerUUID = UUID.fromString(players.get(i));
            val currentPlayer = Bukkit.getPlayer(playerUUID);

            assert currentPlayer != null;
            set(i, new HeadBuilder()
                    .headOwner(playerUUID)
                    .displayName(currentPlayer.displayName()));
        }

    }

    @Override
    public void onClose(Player player) {

    }
}
