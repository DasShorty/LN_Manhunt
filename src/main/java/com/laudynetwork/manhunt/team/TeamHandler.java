package com.laudynetwork.manhunt.team;

import com.laudynetwork.gameengine.game.GameTeamHandler;
import com.laudynetwork.manhunt.Manhunt;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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

    public int getHuntersCount() {
        if (!teams.containsKey("hunters"))
            return 0;
        return teams.get("hunters").size();
    }

    public boolean isManTeamEmpty() {
        if (!this.teams.containsKey("man"))
            return true;
        return this.teams.get("man").isEmpty();
    }

    public boolean isPlayerMan(UUID uuid) {

        if (!this.teams.containsKey("man"))
            return false;

        return this.teams.get("man").contains(uuid);
    }

    public boolean isPlayerHunter(UUID uuid) {

        if (!this.teams.containsKey("hunters"))
            return false;

        return this.teams.get("hunters").contains(uuid);
    }

    private void createDefault(String teamId) {
        if (this.teams.containsKey(teamId))
            return;

        this.teams.put(teamId, List.of());
    }

    private void addToTeam(Player player, String teamId) {
        val uuids = new ArrayList<>(this.teams.get(teamId));
        uuids.add(player.getUniqueId());
        this.teams.put(teamId, uuids);
    }

    private void removeFromTeam(Player player, String teamId) {
        val uuids = new ArrayList<>(this.teams.get(teamId));
        uuids.remove(player.getUniqueId());
        this.teams.put(teamId, uuids);
    }

    public void playerTeam(String teamId, Player player, GameTeamHandler gameTeamHandler) {

        if (isPlayerMan(player.getUniqueId()))
            fromTeam(player, "man", gameTeamHandler);

        if (isPlayerHunter(player.getUniqueId()))
            fromTeam(player, "hunters", gameTeamHandler);

        switch (teamId) {
            case "hunters" -> toTeam(teamId, player, this.huntersData, gameTeamHandler);
            case "man" -> toTeam(teamId, player, this.manData, gameTeamHandler);
            default -> {
                throw new IllegalArgumentException("Invalid teamId (" + teamId + ")!");
            }
        }

    }

    private void fromTeam(Player player, String teamId, GameTeamHandler gameTeamHandler) {
        player.getPersistentDataContainer().remove(this.gameRoleKey);
        removeFromTeam(player, teamId);

        player.setGlowing(false);

        val team = gameTeamHandler.team(teamId);
        if (team == null)
            return;

        team.removePlayer(player);

    }

    private void toTeam(String teamId, Player player, TeamData data, GameTeamHandler gameTeamHandler) {

        if (teamId.equalsIgnoreCase("man"))
            player.setGlowing(true);


        player.getPersistentDataContainer().set(this.gameRoleKey, PersistentDataType.STRING, teamId);
        createDefault(teamId);
        addToTeam(player, teamId);
        gameTeamHandler.team(teamId, player.getScoreboard(), data.prefix(), data.suffix(), data.color());

        val team = gameTeamHandler.team(teamId);

        if (team == null)
            return;
        team.addPlayer(player);
    }

}
