package com.teamnovus.automessage.commands;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.Permission;
import com.teamnovus.automessage.commands.common.BaseCommand;
import com.teamnovus.automessage.commands.common.CommandManager;
import com.teamnovus.automessage.models.Message;
import com.teamnovus.automessage.models.MessageList;
import com.teamnovus.automessage.models.MessageLists;
import com.teamnovus.automessage.util.Utils;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginCommands {

  @BaseCommand(aliases = "reload", desc = "Reload the configuration from the disk.", permission = Permission.COMMAND_RELOAD)
  public void onReloadCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String[] commandArgs) {
    AutoMessage.plugin.loadConfig();

    commandSender.sendMessage(
        Component.text("Configuration reloaded from disk!", CommandManager.LIGHT));
  }

  @BaseCommand(aliases = "add", desc = "Add a list or message to a list.", usage = "<List> [Index] [Message]", min = 1, permission = Permission.COMMAND_ADD)
  public void onAddCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    if (commandArgs.length == 1) {
      if (MessageLists.getExactList(commandArgs[0]) == null) {
        MessageLists.setList(commandArgs[0], new MessageList());

        AutoMessage.plugin.saveConfiguration();

        commandSender.sendMessage(Component.text("List added successfully!", CommandManager.LIGHT));
      } else {
        commandSender.sendMessage(
            Component.text("A list already exists by this name!", CommandManager.ERROR));
      }
    } else {
      final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

      if (list != null) {
        if (commandArgs.length >= 3 && Utils.isInteger(commandArgs[1])) {
          final @NotNull Message message = new Message(Utils.concat(commandArgs, 2, commandArgs.length));

          list.addMessage(Integer.valueOf(commandArgs[1]), message);
        } else {
          final @NotNull Message message = new Message(Utils.concat(commandArgs, 1, commandArgs.length));

          list.addMessage(message);
        }
        AutoMessage.plugin.saveConfiguration();
        commandSender.sendMessage(Component.text("Message added!", CommandManager.LIGHT));
      } else {
        commandSender.sendMessage(
            Component.text("The specified list does not exist!", CommandManager.ERROR));
      }
    }
  }

  @BaseCommand(aliases = "edit", desc = "Edit a message in a list.", usage = "<List> <Index> <Message>", min = 3, permission = Permission.COMMAND_EDIT)
  public void onEditCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      if (Utils.isInteger(commandArgs[1])) {
        final @NotNull Message message = new Message(Utils.concat(commandArgs, 2, commandArgs.length));

        if (list.editMessage(Integer.valueOf(commandArgs[1]), message)) {
          AutoMessage.plugin.saveConfiguration();

          commandSender.sendMessage(Component.text("Message edited!", CommandManager.LIGHT));
        } else {
          commandSender.sendMessage(
              Component.text("The specified index does not exist!", CommandManager.ERROR));
        }
      } else {
        commandSender.sendMessage(
            Component.text("The specified index does not exist!", CommandManager.ERROR));
      }
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "remove", desc = "Remove a list or message from a list.", usage = "<List> [Index]", min = 1, max = 3, permission = Permission.COMMAND_REMOVE)
  public void onRemoveCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    if (commandArgs.length == 1) {
      if (MessageLists.getExactList(commandArgs[0]) != null) {
        MessageLists.setList(commandArgs[0], null);

        AutoMessage.plugin.saveConfiguration();

        commandSender.sendMessage(
            Component.text("List removed successfully!", CommandManager.LIGHT));
      } else {
        commandSender.sendMessage(
            Component.text("The specified list does not exist!", CommandManager.ERROR));
      }
    } else {
      final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

      if (list != null) {
        if (Utils.isInteger(commandArgs[1])) {
          if (list.removeMessage(Integer.valueOf(commandArgs[1]))) {
            MessageLists.schedule();
            AutoMessage.plugin.saveConfiguration();

            commandSender.sendMessage(Component.text("Message removed!", CommandManager.LIGHT));
          } else {
            commandSender.sendMessage(
                Component.text("The specified index does not exist!", CommandManager.ERROR));
          }
        } else {
          commandSender.sendMessage(
              Component.text("The specified index does not exist!", CommandManager.ERROR));
        }
      } else {
        commandSender.sendMessage(
            Component.text("The specified list does not exist!", CommandManager.ERROR));
      }
    }
  }

  @BaseCommand(aliases = "enabled", desc = "Toggle broadcasting for a list.", usage = "<List>", min = 1, max = 1, permission = Permission.COMMAND_ENABLE)
  public void onEnableCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      list.setEnabled(!(list.isEnabled()));

      AutoMessage.plugin.saveConfiguration();

      commandSender.sendMessage(Component.text("Enabled: ", CommandManager.LIGHT)
          .append(Component.text(list.isEnabled(), CommandManager.HIGHLIGHT))
          .append(Component.text("!")));
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "interval", desc = "Set a lists broadcast interval.", usage = "<List> <Interval>", min = 2, max = 2, permission = Permission.COMMAND_INTERVAL)
  public void onIntervalCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      if (Utils.isInteger(commandArgs[1])) {
        list.setInterval(Integer.parseInt(commandArgs[1]));

        MessageLists.schedule();
        AutoMessage.plugin.saveConfiguration();

        commandSender.sendMessage(Component.text("Interval: ", CommandManager.LIGHT)
            .append(Component.text(commandArgs[1], CommandManager.HIGHLIGHT))
            .append(Component.text("!")));
      } else {
        commandSender.sendMessage(
            Component.text("The interval must be an Integer!", CommandManager.ERROR));
      }
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "expiry", desc = "Set a lists expiry time.", usage = "<List> <Expiry>", min = 2, max = 2, permission = Permission.COMMAND_EXPIRY)
  public void onExpiryCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      try {
        if (Utils.isInteger(commandArgs[1])) {
          if (Integer.valueOf(commandArgs[1]).longValue() >= 0) {
            list.setExpiry(System.currentTimeMillis() + Integer.valueOf(commandArgs[1]).longValue());
          } else {
            list.setExpiry(-1L);
          }
        } else {
          list.setExpiry(System.currentTimeMillis() + Utils.parseTime(commandArgs[1]));
        }

        AutoMessage.plugin.saveConfiguration();

        if (list.getExpiry() != -1) {
          commandSender.sendMessage(Component.text("Expires in ", CommandManager.LIGHT).append(
              Component.text(Utils.millisToLongDHMS(list.getExpiry() - System.currentTimeMillis()),
                  CommandManager.HIGHLIGHT).append(Component.text("!"))));
        } else {
          commandSender.sendMessage(Component.text("Expiry disabled!", CommandManager.LIGHT));
        }
      } catch (final NumberFormatException e) {
        commandSender.sendMessage(
            Component.text("Illegal Format. To disable use -1.", CommandManager.ERROR));
      }
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "random", desc = "Set a lists broadcast method.", usage = "<List>", min = 1, max = 1, permission = Permission.COMMAND_RANDOM)
  public void onRandomCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      list.setRandom(!(list.isRandom()));

      AutoMessage.plugin.saveConfiguration();

      commandSender.sendMessage(Component.text("Random: ", CommandManager.LIGHT)
          .append(Component.text(list.isRandom(), CommandManager.HIGHLIGHT))
          .append(Component.text("!")));
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "broadcast", desc = "Broadcast a message from a list.", usage = "<List> <Index>", min = 2, max = 2, permission = Permission.COMMAND_BROADCAST)
  public void onBroadcast(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

    if (list != null) {
      if (Utils.isInteger(commandArgs[1])) {
        final int index = Integer.parseInt(commandArgs[1]);

        if (list.getMessage(index) != null) {
          list.broadcast(index);
        } else {
          commandSender.sendMessage(
              Component.text("The specified index does not exist!", CommandManager.ERROR));
        }
      } else {
        commandSender.sendMessage(
            Component.text("The specified index does not exist!", CommandManager.ERROR));
      }
    } else {
      commandSender.sendMessage(
          Component.text("The specified list does not exist!", CommandManager.ERROR));
    }
  }

  @BaseCommand(aliases = "list", desc = "List all lists or messages in a list.", usage = "[List]", max = 1, permission = Permission.COMMAND_LIST)
  public void onListCmd(final @NotNull CommandSender commandSender, final Command command,
      final String s, final String @NotNull [] commandArgs) {
    if (commandArgs.length == 0) {
      if (!MessageLists.getMessageLists().keySet().isEmpty()) {
        commandSender.sendMessage(Component.text("Available Lists:", CommandManager.LIGHT));

        for (final String key : MessageLists.getMessageLists().keySet()) {
          commandSender.sendMessage(Component.text(" ⏵ " + key, CommandManager.HIGHLIGHT));
        }
      } else {
        commandSender.sendMessage(Component.text("No lists available!", CommandManager.ERROR));
      }
    } else {
      final @Nullable MessageList list = MessageLists.getBestList(commandArgs[0]);

      if (list != null) {
        commandSender.sendMessage(
            Component.text("List '" + MessageLists.getBestKey(commandArgs[0]) + "'",
            CommandManager.LIGHT));

        final List<Message> messages = list.getMessages();
        for (int i = 0; i < messages.size(); i++) {
          commandSender.sendMessage(
              Component.empty().append(Component.text(i + ": ", CommandManager.HIGHLIGHT))
                  .append(Utils.parseLegacyColorAndMiniMessage(messages.get(i).getMessage())));
        }
      } else {
        commandSender.sendMessage(
            Component.text("The specified list does not exist!", CommandManager.ERROR));
      }
    }
  }

}
