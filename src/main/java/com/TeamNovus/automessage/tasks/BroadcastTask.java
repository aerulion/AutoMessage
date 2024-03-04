package com.teamnovus.automessage.tasks;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.models.MessageList;
import com.teamnovus.automessage.models.MessageLists;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastTask implements Runnable {

  private final String name;

  public BroadcastTask(final String name) {
    super();
    this.name = name;
  }

  @Override
  public void run() {
    if (MessageLists.getExactList(this.name) != null && AutoMessage.plugin.getConfig()
        .getBoolean("settings.enabled")) {
      final MessageList list = MessageLists.getExactList(this.name);

      if (list.isEnabled() && list.hasMessages() && !(list.isExpired())) {
        if (Bukkit.getServer().getOnlinePlayers().size() >= AutoMessage.plugin.getConfig()
            .getInt("settings.min-players")) {
          final int index = list.isRandom() ? new Random().nextInt(list.getMessages().size())
              : list.getCurrentIndex();

          for (final @NotNull Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.hasPermission("automessage.receive." + this.name)) {
              list.broadcastTo(index, p);
            }
          }

          if (AutoMessage.plugin.getConfig().getBoolean("settings.log-to-console")) {
            list.broadcastTo(index, Bukkit.getConsoleSender());
          }

          list.setCurrentIndex(index + 1);
        }
      }
    }
  }

}