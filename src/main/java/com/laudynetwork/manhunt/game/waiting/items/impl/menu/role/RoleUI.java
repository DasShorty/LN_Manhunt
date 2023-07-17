package com.laudynetwork.manhunt.game.waiting.items.impl.menu.role;

import com.laudynetwork.gameengine.game.GameTeamHandler;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoleUI extends GUI {

    private final Map<Integer, List<String>> pagination = partition(Bukkit.getOnlinePlayers().stream().map(player -> player.getUniqueId().toString()).toList(), 36);
    private final TeamHandler teamHandler;
    private final GameTeamHandler gameTeamHandler;
    private final ManhuntGame game;
    private int currentPage = 0;


    public RoleUI(Player player, Component displayName, TeamHandler teamHandler, GameTeamHandler gameTeamHandler, ManhuntGame game) {
        super(player, displayName, 45);
        this.teamHandler = teamHandler;
        this.gameTeamHandler = gameTeamHandler;
        this.game = game;
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

        if (pagination.keySet().size() != 1)
            set(36, new ItemBuilder(Material.RED_BED)
                    .displayName(Component.text("vorherige Seite")));

        if (!pagination.containsKey(currentPage))
            currentPage = 0;

        val players = pagination.get(currentPage);

        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        for (int i = 0; i < 35; i++) {

            if (players.size() - 1 < i)
                continue;

            val playerUUID = UUID.fromString(players.get(i));
            val currentPlayer = Bukkit.getPlayer(playerUUID);

            assert currentPlayer != null;
            set(i, new HeadBuilder()
                    .lore(getLore(language, "game.ui.game.player.lore"))
                    .headOwner(playerUUID)
                    .displayName(game.getMsgApi().getTranslation(language, "game.ui.game.player.displayname", Placeholder.unparsed("player", currentPlayer.getName()))
                    ), (clicker, itemStack, clickType) -> {

                SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                val owningPlayer = itemMeta.getOwningPlayer();

                assert owningPlayer != null;
                val clickedPlayer = Bukkit.getPlayer(owningPlayer.getUniqueId());
                assert clickedPlayer != null;

                if (clickType == ClickType.LEFT) {

                    val clickedLang = new NetworkPlayer(game.getDatabase(), clickedPlayer.getUniqueId()).getLanguage();

                    if (this.teamHandler.isPlayerMan(owningPlayer.getUniqueId())) {

                        this.teamHandler.playerTeam("hunters", Objects.requireNonNull(clickedPlayer), this.gameTeamHandler);
                        clicker.sendMessage(game.getMsgApi().getMessage(language, "game.demote.other", Placeholder.unparsed("player", clickedPlayer.getName())));
                        clickedPlayer.sendMessage(game.getMsgApi().getMessage(clickedLang, "game.demote"));

                    } else {

                        this.teamHandler.playerTeam("man", Objects.requireNonNull(clickedPlayer), this.gameTeamHandler);
                        clicker.sendMessage(game.getMsgApi().getMessage(language, "game.promote.other", Placeholder.unparsed("player", clickedPlayer.getName())));
                        clickedPlayer.sendMessage(game.getMsgApi().getMessage(clickedLang, "game.promote"));

                    }
                }

                if (clickType == ClickType.RIGHT)
                    clicker.teleportAsync(clickedPlayer.getLocation());


                return GUIItem.GUIAction.CANCEL;
            });
        }

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
    public void onClose(Player player) {

    }
}
