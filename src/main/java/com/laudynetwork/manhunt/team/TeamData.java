package com.laudynetwork.manhunt.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record TeamData(String id, Component prefix, Component suffix, NamedTextColor color) {
}
