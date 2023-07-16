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
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

        set(12, new ItemBuilder(Material.CLOCK)
                .displayName(Component.text("Spiel starten")), (clickedPlayer, itemStack, clickType) -> {

            val language = new NetworkPlayer(game.getDatabase(), clickedPlayer.getUniqueId()).getLanguage();

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

            return GUIItem.GUIAction.CANCEL;
        });

        set(14, new ItemBuilder(Material.LECTERN)
                .displayName(Component.text("Rollen")), (clickedPlayer, itemStack, clickType) -> {

            val language = new NetworkPlayer(game.getDatabase(), clickedPlayer.getUniqueId()).getLanguage();

            if (this.teamHandler.isPlayerHunter(clickedPlayer.getUniqueId())) {
                clickedPlayer.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 30f, 1f));
                clickedPlayer.sendMessage(game.getMsgApi().getMessage(language, "command.missing.permission"));
                return GUIItem.GUIAction.CANCEL;
            }

                Manhunt.getINSTANCE().getGuiHandler().openDelayed(clickedPlayer, new RoleUI(clickedPlayer, Component.text("Rollen"), teamHandler, this.gameTeamHandler), CloseReason.NEW_UI);
            return GUIItem.GUIAction.CANCEL;
        });

    }

    @Override
    public void onClose(Player player) {

    }
}
