package com.laudynetwork.manhunt.game.waiting.items.impl.menu.role;

import com.laudynetwork.gameengine.game.GameTeamHandler;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoleUI extends GUI {

    private final Map<Integer, List<String>> pagination = partition(Bukkit.getOnlinePlayers().stream().map(player -> player.getUniqueId().toString()).toList(), 36);
    private final TeamHandler teamHandler;
    private final GameTeamHandler gameTeamHandler;
    private int currentPage = 0;


    public RoleUI(Player player, Component displayName, TeamHandler teamHandler, GameTeamHandler gameTeamHandler) {
        super(player, displayName, 45);
        this.teamHandler = teamHandler;
        this.gameTeamHandler = gameTeamHandler;
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
                    .displayName(currentPlayer.displayName().color(NamedTextColor.GRAY)), (clicker, itemStack, clickType) -> {

                SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                val owningPlayer = itemMeta.getOwningPlayer();

                // LEFT: promote/demote man
                // RIGHT: ignore

                assert owningPlayer != null;
                val clickedPlayer = Bukkit.getPlayer(owningPlayer.getUniqueId());
                assert clickedPlayer != null;

                if (clickType == ClickType.LEFT) {

                    if (this.teamHandler.isPlayerMan(owningPlayer.getUniqueId())) {

                        this.teamHandler.playerTeam("hunters", Objects.requireNonNull(clickedPlayer), this.gameTeamHandler);
                        clicker.sendMessage(Component.text("Spieler " + owningPlayer.getName() + " ist nun ein Hunter!"));
                        clickedPlayer.sendMessage(Component.text("Du bist nun ein Hunter!"));

                    } else {

                        this.teamHandler.playerTeam("man", Objects.requireNonNull(clickedPlayer), this.gameTeamHandler);
                        clicker.sendMessage(Component.text("Spieler " + owningPlayer.getName() + " ist nun ein Man!"));
                        clickedPlayer.sendMessage(Component.text("Du bist nun ein Man!"));

                    }
                }

                if (clickType == ClickType.RIGHT) {
                    clicker.teleportAsync(clickedPlayer.getLocation());
                }


                return GUIItem.GUIAction.CANCEL;
            });
        }

    }

    @Override
    public void onClose(Player player) {

    }
}
