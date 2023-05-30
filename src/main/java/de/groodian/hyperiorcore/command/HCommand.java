package de.groodian.hyperiorcore.command;

import net.kyori.adventure.text.Component;

public record HCommand(String name, String description, Component prefix, String permission) {
}
