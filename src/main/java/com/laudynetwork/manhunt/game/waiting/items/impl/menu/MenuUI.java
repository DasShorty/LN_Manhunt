package com.laudynetwork.manhunt.game.waiting.items.impl.menu;

import com.laudynetwork.gameengine.game.GameTeamHandler;
import com.laudynetwork.gameengine.game.gamestate.GameState;
import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.manhunt.ManhuntGame;
import com.laudynetwork.manhunt.game.waiting.items.impl.menu.role.RoleUI;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.gui.event.CloseReason;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MenuUI extends GUI {

    private final TeamHandler teamHandler;
    private final GameTeamHandler gameTeamHandler;
    private final ManhuntGame game;

    public MenuUI(Player player, Component displayName, TeamHandler teamHandler, GameTeamHandler gameTeamHandler, ManhuntGame game) {
        super(player, displayName, 27);
        this.teamHandler = teamHandler;
        this.gameTeamHandler = gameTeamHandler;
        this.game = game;
    }

    @Override
    public void generateGUI(Player player) {

        val language = new NetworkPlayer(game.getDatabase(), player.getUniqueId()).getLanguage();

        set(12, new ItemBuilder(Material.CLOCK)
                .lore(getLore(language, "game.ui.game.start.lore"))
                .displayName(game.getMsgApi().getTranslation(language, "game.ui.game.start.displayname")), (clickedPlayer, itemStack, clickType) -> {

            if (teamHandler.isPlayerHunter(clickedPlayer.getUniqueId())) {
                clickedPlayer.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 30f, 1f));
                clickedPlayer.sendMessage(game.getMsgApi().getMessage(language, "command.missing.permission"));
                return GUIItem.GUIAction.CLOSE;
            }

            if (!game.phaseRequirement(GameState.STARTING)) {
                clickedPlayer.sendMessage(game.getMsgApi().getMessage(language, "game.requirement.starting"));
                return GUIItem.GUIAction.CLOSE;
            }

            game.loadPhase(GameState.STARTING);

            return GUIItem.GUIAction.CLOSE;
        });

        set(14, new ItemBuilder(Material.LECTERN)
                .lore(getLore(language, "game.ui.game.role.lore"))
                .displayName(game.getMsgApi().getTranslation(language, "game.ui.game.role.displayname")), (clickedPlayer, itemStack, clickType) -> {

            if (this.teamHandler.isPlayerHunter(clickedPlayer.getUniqueId())) {
                clickedPlayer.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 30f, 1f));
                clickedPlayer.sendMessage(game.getMsgApi().getMessage(language, "command.missing.permission"));
                return GUIItem.GUIAction.CANCEL;
            }

                Manhunt.getINSTANCE().getGuiHandler().openDelayed(clickedPlayer, new RoleUI(clickedPlayer, game.getMsgApi().getTranslation(language, "game.ui.game.role.title"), teamHandler, this.gameTeamHandler, game), CloseReason.NEW_UI);
            return GUIItem.GUIAction.CANCEL_AND_NEW;
        });

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
