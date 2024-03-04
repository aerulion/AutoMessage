package com.teamnovus.automessage.models;

import com.teamnovus.automessage.util.Utils;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageList {

  private boolean enabled = true;
  private int interval = 45;
  private long expiry = -1L;
  private boolean random = false;
  private List<Message> messages = new LinkedList<>();

  private transient int currentIndex = 0;

  public MessageList() {
    super();
    this.messages.add(new Message("First message in the list!"));
    this.messages.add(new Message("&aSecond message in the list with formatters!"));
    this.messages.add(new Message("&bThird message in the list with formatters and a \nnew line!"));
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public int getInterval() {
    return this.interval;
  }

  public void setInterval(final int interval) {
    this.interval = interval;
  }

  public long getExpiry() {
    return this.expiry;
  }

  public void setExpiry(final long expiry) {
    this.expiry = expiry;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() >= this.expiry && this.expiry != -1;
  }

  public boolean isRandom() {
    return this.random;
  }

  public void setRandom(final boolean random) {
    this.random = random;
  }

  public List<Message> getMessages() {
    return this.messages;
  }

  public void setMessages(final List<Message> messages) {
    this.messages = messages;
  }

  public void addMessage(final Message message) {
    this.messages.add(message);
  }

  public @Nullable Message getMessage(final @NotNull Integer index) {
    try {
      return this.messages.get(index);
    } catch (final IndexOutOfBoundsException e) {
      return null;
    }
  }

  public void addMessage(final @NotNull Integer index, final Message message) {
    try {
      this.messages.add(index, message);
    } catch (final IndexOutOfBoundsException e) {
      this.messages.add(message);
    }
  }

  public boolean editMessage(final @NotNull Integer index, final Message message) {
    try {
      return this.messages.set(index, message) != null;
    } catch (final IndexOutOfBoundsException e) {
      return false;
    }
  }

  public boolean removeMessage(final @NotNull Integer index) {
    try {
      return this.messages.remove(index.intValue()) != null;
    } catch (final IndexOutOfBoundsException e) {
      return false;
    }
  }

  public boolean hasMessages() {
    return !this.messages.isEmpty();
  }

  public int getCurrentIndex() {
    return this.currentIndex;
  }

  public void setCurrentIndex(final int index) {
    this.currentIndex = index;

    if (this.currentIndex >= this.messages.size() || this.currentIndex < 0) {
      this.currentIndex = 0;
    }
  }

  public void broadcast(final int index) {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      broadcastTo(index, player);
    }

    broadcastTo(index, Bukkit.getConsoleSender());
  }

  public void broadcastTo(final int index, final CommandSender to) {
    final @Nullable Message message = getMessage(index);

    if (message != null) {
      final @NotNull List<String> messages = message.getMessages();
      final @NotNull List<String> commands = message.getCommands();

      for (@NotNull String m : messages) {
        if (to instanceof Player) {
          if (m.contains("{NAME}")) {
            m = m.replace("{NAME}", to.getName());
          }
          if (m.contains("{DISPLAY_NAME}")) {
            m = m.replace("{DISPLAY_NAME}", ((Player) to).getDisplayName());
          }
          if (m.contains("{WORLD}")) {
            m = m.replace("{WORLD}", ((Entity) to).getWorld().getName());
          }
          if (m.contains("{BIOME}")) {
            m = m.replace("{BIOME}", ((Entity) to).getLocation().getBlock().getBiome().toString());
          }
        } else if (to instanceof ConsoleCommandSender) {
          if (m.contains("{NAME}")) {
            m = m.replace("{NAME}", to.getName());
          }
          if (m.contains("{DISPLAY_NAME}")) {
            m = m.replace("{DISPLAY_NAME}", to.getName());
          }
          if (m.contains("{WORLD}")) {
            m = m.replace("{WORLD}", "UNKNOWN");
          }
          if (m.contains("{BIOME}")) {
            m = m.replace("{BIOME}", "UNKNOWN");
          }
        }

        if (m.contains("{ONLINE}")) {
          m = m.replace("{ONLINE}", Bukkit.getServer().getOnlinePlayers().size() + "");
        }
        if (m.contains("{MAX_ONLINE}")) {
          m = m.replace("{MAX_ONLINE}", Bukkit.getServer().getMaxPlayers() + "");
        }
        if (m.contains("{UNIQUE_PLAYERS}")) {
          m = m.replace("{UNIQUE_PLAYERS}", Bukkit.getServer().getOfflinePlayers().length + "");
        }

        if (m.contains("{YEAR}")) {
          m = m.replace("{YEAR}", Calendar.getInstance().get(Calendar.YEAR) + "");
        }
        if (m.contains("{MONTH}")) {
          m = m.replace("{MONTH}", Calendar.getInstance().get(Calendar.MONTH) + "");
        }
        if (m.contains("{WEEK_OF_MONTH}")) {
          m = m.replace("{WEEK_OF_MONTH}", Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) + "");
        }
        if (m.contains("{WEEK_OF_YEAR}")) {
          m = m.replace("{WEEK_OF_YEAR}", Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "");
        }
        if (m.contains("{DAY_OF_WEEK}")) {
          m = m.replace("{DAY_OF_WEEK}", Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "");
        }
        if (m.contains("{DAY_OF_MONTH}")) {
          m = m.replace("{DAY_OF_MONTH}", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
        }
        if (m.contains("{DAY_OF_YEAR}")) {
          m = m.replace("{DAY_OF_YEAR}", Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "");
        }
        if (m.contains("{HOUR}")) {
          m = m.replace("{HOUR}", Calendar.getInstance().get(Calendar.HOUR) + "");
        }
        if (m.contains("{HOUR_OF_DAY}")) {
          m = m.replace("{HOUR_OF_DAY}", Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "");
        }
        if (m.contains("{MINUTE}")) {
          m = m.replace("{MINUTE}", Calendar.getInstance().get(Calendar.MINUTE) + "");
        }
        if (m.contains("{SECOND}")) {
          m = m.replace("{SECOND}", Calendar.getInstance().get(Calendar.SECOND) + "");
        }

        to.sendMessage(Utils.parseLegacyColorAndMiniMessage(m));
      }

      for (final @NotNull String command : commands) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceFirst("/", ""));
      }
    }
  }
}
