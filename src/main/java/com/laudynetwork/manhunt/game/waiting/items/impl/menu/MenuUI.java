package com.laudynetwork.manhunt.game.waiting.items.impl.menu;

import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.manhunt.game.waiting.items.impl.menu.role.RoleUI;
import com.laudynetwork.manhunt.team.TeamHandler;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.gui.event.CloseReason;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MenuUI extends GUI {

    private final TeamHandler teamHandler;

    public MenuUI(Player player, Component displayName, TeamHandler teamHandler) {
        super(player, displayName, 27);
        this.teamHandler = teamHandler;
    }

    @Override
    public void generateGUI(Player player) {

        set(12, new ItemBuilder(Material.CLOCK)
                .displayName(Component.text("Spiel starten")));

        set(14, new ItemBuilder(Material.LECTERN)
                .displayName(Component.text("Rollen")), (clickedPlayer, itemStack, clickType) -> {

            if (this.teamHandler.isPlayerHunter(clickedPlayer.getUniqueId())) {
                clickedPlayer.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 30f, 1f));
                return GUIItem.GUIAction.CANCEL;
            }

                Manhunt.getINSTANCE().getGuiHandler().openDelayed(clickedPlayer, new RoleUI(clickedPlayer, Component.text("Rollen")), CloseReason.NEW_UI);
            return GUIItem.GUIAction.CANCEL;
        });

    }

    @Override
    public void onClose(Player player) {

    }
}
