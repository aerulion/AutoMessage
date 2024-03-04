package com.teamnovus.automessage.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class Utils {

  public static final long ONE_SECOND = 1000;
  public static final long ONE_MINUTE = ONE_SECOND * 60;
  public static final long ONE_HOUR = ONE_MINUTE * 60;
  public static final long ONE_DAY = ONE_HOUR * 24;

  private static final @NotNull MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private Utils() {
    super();
  }

  /**
   * Parses the raw text with the legacy color codes and mini message styling applied.
   *
   * @param rawText the raw the text to parse
   * @return the parsed component
   */
  public static @NotNull Component parseLegacyColorAndMiniMessage(final @NotNull String rawText) {
    return MINI_MESSAGE.deserialize(replaceLegacyColorCode(rawText));
  }

  private static @NotNull String replaceLegacyColorCode(final @NotNull String rawText) {
    return rawText.replace("&0", "<reset><black>").replace("&1", "<reset><dark_blue>")
        .replace("&2", "<reset><dark_green>").replace("&3", "<reset><dark_aqua>")
        .replace("&4", "<reset><dark_red>").replace("&5", "<reset><dark_purple>")
        .replace("&6", "<reset><gold>").replace("&7", "<reset><gray>")
        .replace("&8", "<reset><dark_gray>").replace("&9", "<reset><blue>")
        .replace("&a", "<reset><green>").replace("&b", "<reset><aqua>")
        .replace("&c", "<reset><red>").replace("&d", "<reset><light_purple>")
        .replace("&e", "<reset><yellow>").replace("&f", "<reset><white>")
        .replace("&k", "<obfuscated>").replace("&l", "<bold>").replace("&m", "<strikethrough>")
        .replace("&n", "<underlined>").replace("&o", "<italic>").replace("&r", "<reset>");
  }

  public static boolean isInteger(final @NotNull String s) {
    try {
      Integer.valueOf(s);
      return true;
    } catch (final NumberFormatException e) {
      return false;
    }
  }

  public static @NotNull String concat(final String @NotNull [] s, final int start, final int end) {
    final String @NotNull [] args = Arrays.copyOfRange(s, start, end);
    return String.join(" ", args);
  }

  public static @NotNull String millisToLongDHMS(long duration) {
    final @NotNull StringBuilder res = new StringBuilder();
    long temp;
    if (duration >= ONE_SECOND) {
      temp = duration / ONE_DAY;
      if (temp > 0) {
        duration -= temp * ONE_DAY;
        res.append(temp).append(" day").append(temp > 1 ? "s" : "")
            .append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_HOUR;
      if (temp > 0) {
        duration -= temp * ONE_HOUR;
        res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
            .append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_MINUTE;
      if (temp > 0) {
        duration -= temp * ONE_MINUTE;
        res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
      }

      if (!res.toString().isEmpty() && (duration >= ONE_SECOND)) {
        res.append(" and ");
      }

      temp = duration / ONE_SECOND;
      if (temp > 0) {
        res.append(temp).append(" second").append(temp > 1 ? "s" : "");
      }
      return res.toString();
    } else {
      return "0 second";
    }

  }

  public static @NotNull Long parseTime(final @NotNull String timeString)
      throws NumberFormatException {
    long time;

    int weeks = 0;
    int days = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;

    final @NotNull Pattern p = Pattern.compile("\\d+[a-z]{1}");
    final @NotNull Matcher m = p.matcher(timeString);
    boolean result = m.find();

    while (result) {
      final String argument = m.group();

      if (argument.endsWith("w")) {
        weeks = Integer.parseInt(argument.substring(0, argument.length() - 1));
      } else if (argument.endsWith("d")) {
        days = Integer.parseInt(argument.substring(0, argument.length() - 1));
      } else if (argument.endsWith("h")) {
        hours = Integer.parseInt(argument.substring(0, argument.length() - 1));
      } else if (argument.endsWith("m")) {
        minutes = Integer.parseInt(argument.substring(0, argument.length() - 1));
      } else if (argument.endsWith("s")) {
        seconds = Integer.parseInt(argument.substring(0, argument.length() - 1));
      }

      result = m.find();
    }

    time = seconds;
    time += minutes * 60L;
    time += hours * 3600L;
    time += days * 86400L;
    time += weeks * 604800L;

    // convert to milliseconds
    time = time * 1000;

    return time;

  }
}
