package com.teamnovus.automessage.commands;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.Permission;
import com.teamnovus.automessage.commands.common.BaseCommand;
import com.teamnovus.automessage.commands.common.CommandManager;
import com.teamnovus.automessage.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DefaultCommands {

  @BaseCommand(aliases = {"help",
      "?"}, desc = "View all commands and their info.", usage = "[Page]", permission = Permission.NONE, min = 0, max = 1, hidden = true)
  public void helpCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    // help [Page]
    final int maxLines = 6;

    if (commandArgs.length != 0 && (!(Utils.isInteger(commandArgs[0]))
        || Math.abs(Integer.parseInt(commandArgs[0])) * maxLines - maxLines
        >= CommandManager.getCommands().size())) {
      commandSender.sendMessage(
          Component.text("The specified page was not found.", CommandManager.ERROR));
      return;
    }

    final int page = commandArgs.length == 0 ? 1 : Math.abs(Integer.parseInt(commandArgs[0]));
    int total = 0;
    commandSender.sendMessage(Component.text("__________________.[ ", CommandManager.EXTRA)
        .append(Component.text(AutoMessage.plugin.getName(), CommandManager.HIGHLIGHT))
        .append(Component.text(" ].__________________")));

    commandSender.sendMessage(Component.text("Required: < > Optional: [ ]", NamedTextColor.GRAY));
    for (int i = maxLines * page - maxLines;
        i < CommandManager.getCommands().size() && total < maxLines - 1; i++) {
      final BaseCommand baseCommand = CommandManager.getCommands().get(i);
      if (!(baseCommand.hidden()) && Permission.has(baseCommand.permission(), commandSender)) {
        commandSender.sendMessage(CommandManager.EXTRA + " - " + CommandManager.DARK + "/" + s + " "
            + baseCommand.aliases()[0] + (!(baseCommand.usage().isEmpty()) ? " "
            + baseCommand.usage() : "") + ": " + CommandManager.LIGHT + baseCommand.desc());
        total++;
      }
    }
    commandSender.sendMessage(Component.text("For help type: ", CommandManager.LIGHT)
        .append(Component.text("/" + s + " help [Page]", CommandManager.HIGHLIGHT)));
    commandSender.sendMessage(Component.text("---------------------------------------------------",
        CommandManager.EXTRA));
  }

}
