package com.teamnovus.automessage.commands.common;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.Permission;
import com.teamnovus.automessage.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseCommandExecutor implements CommandExecutor, TabCompleter {

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public boolean onCommand(final @NotNull CommandSender commandSender,
      final @NotNull Command command, final @NotNull String s, final String @NotNull [] strings) {
    if (strings.length == 0) {
      commandSender.sendMessage(Component.text("__________________.[ ", CommandManager.EXTRA)
          .append(Component.text(AutoMessage.plugin.getName(), CommandManager.HIGHLIGHT))
          .append(Component.text(" ].__________________")));
      commandSender.sendMessage(Component.text("Description: ", CommandManager.DARK).append(
          Component.text(AutoMessage.plugin.getPluginMeta().getDescription(),
              CommandManager.LIGHT)));
      commandSender.sendMessage(Component.text("Author: ", CommandManager.DARK).append(
          Component.text(AutoMessage.plugin.getPluginMeta().getAuthors().getFirst(),
              CommandManager.LIGHT)));
      commandSender.sendMessage(Component.text("Version: ", CommandManager.DARK).append(
          Component.text(AutoMessage.plugin.getPluginMeta().getVersion(), CommandManager.LIGHT)));
      commandSender.sendMessage(
          Component.text("---------------------------------------------------",
              CommandManager.EXTRA));
      return true;
    }

    if (CommandManager.getCommand(strings[0]) == null) {
      commandSender.sendMessage(
          Component.text("The specified command was not found!", CommandManager.ERROR));
      return true;
    }

    final @Nullable BaseCommand baseCommand = CommandManager.getCommand(strings[0]);
    final Object @NotNull [] commandArgs = Utils.skipFirstElement(strings);

    if (commandSender instanceof Player && !(baseCommand.player())) {
      commandSender.sendMessage(
          Component.text("This command cannot be ran as a player!", CommandManager.ERROR));
      return true;
    }

    if (commandSender instanceof ConsoleCommandSender && !(baseCommand.console())) {
      commandSender.sendMessage(
          Component.text("This command cannot be ran from the console!", CommandManager.ERROR));
      return true;
    }

    if (baseCommand.permission() != null && baseCommand.permission() != Permission.NONE
        && !(Permission.has(baseCommand.permission(), commandSender))) {
      commandSender.sendMessage(
          Component.text("You do not have permission for this command!", CommandManager.ERROR));
      return true;
    }

    if ((commandArgs.length < baseCommand.min()) || (commandArgs.length > baseCommand.max()
        && baseCommand.max() != -1)) {
      commandSender.sendMessage(Component.text(
          "Usage: /" + s + " " + baseCommand.aliases()[0] + " " + baseCommand.usage(),
          CommandManager.ERROR));
      return true;
    }

    CommandManager.execute(baseCommand, commandSender, command, s, commandArgs);
    return true;
  }

  @Override
  public List<String> onTabComplete(final @NotNull CommandSender commandSender,
      final @NotNull Command command, final @NotNull String s, final String[] strings) {
    final List<String> list = new ArrayList<>();

    for (final @NotNull BaseCommand baseCommand : CommandManager.getCommands()) {
      Collections.addAll(list, baseCommand.aliases());
    }

    return list;
  }

}