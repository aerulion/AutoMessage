package com.teamnovus.automessage.models;

import com.teamnovus.automessage.tasks.BroadcastTask;
import com.teamnovus.automessage.AutoMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MessageLists {

  private static HashMap<String, MessageList> lists = new HashMap<>();

  private MessageLists() {
    super();
  }

  public static Map<String, MessageList> getMessageLists() {
    return lists;
  }

  public static void setMessageLists(final HashMap<String, MessageList> messageLists) {
    lists = messageLists;
  }

  public static MessageList getExactList(final String name) {
    return lists.get(name);
  }

  public static @Nullable MessageList getBestList(final @NotNull String name) {
    for (final Entry<String, MessageList> entry : lists.entrySet()) {
      if (entry.getKey().startsWith(name)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public static @Nullable String getBestKey(final @NotNull String name) {
    for (final @NotNull String key : lists.keySet()) {
      if (key.startsWith(name)) {
        return key;
      }
    }

    return null;
  }

  public static void setList(final String key, final @Nullable MessageList value) {
    if (value == null) {
      lists.remove(key);
    } else {
      lists.put(key, value);
    }

    schedule();
  }

  public static void clear() {
    lists.clear();
  }

  public static void schedule() {
    unschedule();

    for (final @NotNull Entry<String, MessageList> entry : lists.entrySet()) {
      final MessageList list = lists.get(entry.getKey());

      Bukkit.getServer().getScheduler()
          .scheduleSyncRepeatingTask(AutoMessage.plugin, new BroadcastTask(entry.getKey()),
              20L * list.getInterval(), 20L * list.getInterval());
    }
  }

  public static void unschedule() {
    Bukkit.getScheduler().cancelTasks(AutoMessage.plugin);
  }
}
