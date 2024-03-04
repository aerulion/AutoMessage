package com.teamnovus.automessage;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public enum Permission {
  COMMAND_RELOAD("commands.reload"),
  COMMAND_ADD("commands.add"),
  COMMAND_EDIT("commands.edit"),
  COMMAND_REMOVE("commands.remove"),
  COMMAND_ENABLE("commands.enable"),
  COMMAND_INTERVAL("commands.interval"),
  COMMAND_EXPIRY("commands.expiry"),
  COMMAND_RANDOM("commands.random"),
  COMMAND_BROADCAST("commands.broadcast"),
  COMMAND_LIST("commands.list"),
  NONE("");

  private final String node;

  Permission(final String node) {
    this.node = node;
  }

  private static @NotNull String getPermission(final @NotNull Permission permission) {
    return "automessage." + permission.node;
  }

  public static boolean has(final @NotNull Permission permission,
      final @NotNull CommandSender target) {
    return target.hasPermission(getPermission(permission));
  }

}