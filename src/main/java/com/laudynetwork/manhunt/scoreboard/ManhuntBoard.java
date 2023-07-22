package com.laudynetwork.manhunt.scoreboard;

import com.laudynetwork.manhunt.Manhunt;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;

public class ManhuntBoard {

    private final Sidebar sidebar;
    private final MessageAPI msgApi = new MessageAPI(Manhunt.getINSTANCE().getMsgCache(), MessageAPI.PrefixType.MANHUNT);

    public ManhuntBoard(ScoreboardLibrary scoreboardLibrary) {

        this.sidebar = scoreboardLibrary.createSidebar();

        val lines = SidebarComponent.builder()
                .addDynamicLine(this::date)
                .addBlankLine()
                .addDynamicLine(this::manKills)
                .addBlankLine()
                .addDynamicLine(this::hunterDeaths)
                .addStaticLine(Component.text("LAUDYNETWORK")).build();

        val layout = new ComponentSidebarLayout(SidebarComponent.staticLine(msgApi.getTranslation("en", "game.sidebar.title")), lines);
        layout.apply(sidebar);

    }

    private Component hunterDeaths() {
        return Component.text(2);
    }

    private Component manKills() {
        return Component.text("10 Kills");
    }

    private Component date() {
        return Component.text("date");
    }
}
