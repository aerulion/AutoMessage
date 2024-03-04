package com.teamnovus.automessage.commands.common;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandManager {

  public static final TextColor LIGHT = NamedTextColor.GREEN;
  public static final TextColor DARK = NamedTextColor.DARK_GREEN;
  public static final TextColor HIGHLIGHT = NamedTextColor.AQUA;
  public static final TextColor EXTRA = NamedTextColor.DARK_RED;
  public static final TextColor ERROR = NamedTextColor.RED;

  private static final Map<BaseCommand, Method> COMMANDS = new LinkedHashMap<>();

  private CommandManager() {
    super();
  }

  public static void register(final @NotNull Class<?> clazz) {
    final Method @NotNull [] methods = clazz.getMethods();

    for (final @NotNull Method method : methods) {
      if (method.isAnnotationPresent(BaseCommand.class)) {
        COMMANDS.put(method.getAnnotation(BaseCommand.class), method);
      }
    }
  }

  public static @NotNull LinkedList<BaseCommand> getCommands() {
    final @NotNull LinkedList<BaseCommand> baseCommands = new LinkedList<>(COMMANDS.keySet());

    return baseCommands;
  }

  public static @Nullable BaseCommand getCommand(final @NotNull String label) {
    for (final @NotNull BaseCommand command : COMMANDS.keySet()) {
      for (final String alias : command.aliases()) {
        if (label.equalsIgnoreCase(alias)) {
          return command;
        }
      }
    }

    return null;
  }

  public static void execute(final BaseCommand command, final Object... args) {
    try {
      COMMANDS.get(command).invoke(COMMANDS.get(command).getDeclaringClass().newInstance(), args);
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

}
