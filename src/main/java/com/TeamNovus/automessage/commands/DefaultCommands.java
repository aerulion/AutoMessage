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
  public void helpCmd(final @NotNull CommandSender sender, final Command cmd,
      final String commandLabel, final String @NotNull [] args) {
    // help [Page]
    final int maxLines = 6;

    if (args.length != 0 && (!(Utils.isInteger(args[0]))
        || Math.abs(Integer.parseInt(args[0])) * maxLines - maxLines >= CommandManager.getCommands()
        .size())) {
      sender.sendMessage(Component.text("The specified page was not found.", CommandManager.ERROR));
      return;
    }

    final int page = args.length == 0 ? 1 : Math.abs(Integer.parseInt(args[0]));
    int total = 0;
    sender.sendMessage(Component.text("__________________.[ ", CommandManager.EXTRA)
        .append(Component.text(AutoMessage.plugin.getName(), CommandManager.HIGHLIGHT))
        .append(Component.text(" ].__________________")));

    sender.sendMessage(Component.text("Required: < > Optional: [ ]", NamedTextColor.GRAY));
    for (int i = maxLines * page - maxLines;
        i < CommandManager.getCommands().size() && total < maxLines - 1; i++) {
      final BaseCommand command = CommandManager.getCommands().get(i);
      if (!(command.hidden()) && Permission.has(command.permission(), sender)) {
        sender.sendMessage(
            CommandManager.EXTRA + " - " + CommandManager.DARK + "/" + commandLabel + " "
                + command.aliases()[0] + (!(command.usage().isEmpty()) ? " " + command.usage() : "")
                + ": " + CommandManager.LIGHT + command.desc());
        total++;
      }
    }
    sender.sendMessage(Component.text("For help type: ", CommandManager.LIGHT)
        .append(Component.text("/" + commandLabel + " help [Page]", CommandManager.HIGHLIGHT)));
    sender.sendMessage(Component.text("---------------------------------------------------",
        CommandManager.EXTRA));
  }

}
