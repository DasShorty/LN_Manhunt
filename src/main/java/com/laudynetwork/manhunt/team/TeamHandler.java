package com.laudynetwork.manhunt.team;

import com.laudynetwork.manhunt.Manhunt;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeamHandler {

    private final TeamData huntersData;
    private final TeamData manData;

    @Getter
    private final NamespacedKey gameRoleKey = new NamespacedKey(Manhunt.getINSTANCE(), "manhunt-role");

    private final Map<String, List<UUID>> teams = new HashMap<>();

    public TeamHandler() {
        this.huntersData = new TeamData("hunters", Component.empty(), Component.empty(), NamedTextColor.RED);
        this.manData = new TeamData("man", Component.empty(), Component.empty(), NamedTextColor.GREEN);
    }

    public boolean isManTeamEmpty() {
        return this.teams.get("man").isEmpty();
    }

    private void createDefault(String teamId) {
        if (this.teams.containsKey(teamId))
            return;

        this.teams.put(teamId, List.of());
    }

    private void addToTeam(Player player, String teamId) {
        val uuids = this.teams.get(teamId);
        uuids.add(player.getUniqueId());
        this.teams.put(teamId, uuids);
    }

    public void playerTeam(String teamId, Player player) {

        switch (teamId) {
            case "hunters" -> toTeam(teamId, player, this.huntersData);
            case "man" -> toTeam(teamId, player, this.manData);
            default -> {
                throw new IllegalArgumentException("Invalid teamId (" + teamId + ")!");
            }
        }

    }

    private void toTeam(String teamId, Player player, TeamData data) {
        player.getPersistentDataContainer().set(this.gameRoleKey, PersistentDataType.STRING, teamId);
        createDefault(teamId);
        addToTeam(player, teamId);
        this.teamHandler.team(teamId, player.getScoreboard(), data.prefix(), data.suffix(), data.color());
    }

}
