package com.TeamNovus.AutoMessage;

import com.TeamNovus.AutoMessage.Commands.Common.BaseCommandExecutor;
import com.TeamNovus.AutoMessage.Commands.Common.CommandManager;
import com.TeamNovus.AutoMessage.Commands.DefaultCommands;
import com.TeamNovus.AutoMessage.Commands.PluginCommands;
import com.TeamNovus.AutoMessage.Models.Message;
import com.TeamNovus.AutoMessage.Models.MessageList;
import com.TeamNovus.AutoMessage.Models.MessageLists;
import com.TeamNovus.AutoMessage.Util.Metrics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoMessage extends JavaPlugin {

  public static AutoMessage plugin;

  public boolean loadConfig() {
    if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
      saveDefaultConfig();
    }

    try {
      new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
    } catch (Exception e) {
      System.out.println("--- --- --- ---");
      System.out.println("There was an error loading your configuration.");
      System.out.println("A detailed description of your error is shown below.");
      System.out.println("--- --- --- ---");
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    reloadConfig();

    MessageLists.clear();

    for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
      MessageList list = new MessageList();

			if (getConfig().contains("message-lists." + key + ".enabled")) {
				list.setEnabled(getConfig().getBoolean("message-lists." + key + ".enabled"));
			}

			if (getConfig().contains("message-lists." + key + ".interval")) {
				list.setInterval(getConfig().getInt("message-lists." + key + ".interval"));
			}

			if (getConfig().contains("message-lists." + key + ".expiry")) {
				list.setExpiry(getConfig().getLong("message-lists." + key + ".expiry"));
			}

			if (getConfig().contains("message-lists." + key + ".random")) {
				list.setRandom(getConfig().getBoolean("message-lists." + key + ".random"));
			}

      LinkedList<Message> finalMessages = new LinkedList<Message>();

      if (getConfig().contains("message-lists." + key + ".messages")) {
        ArrayList<Object> messages = (ArrayList<Object>) getConfig().getList(
            "message-lists." + key + ".messages");

        for (Object m : messages) {
          if (m instanceof String) {
            finalMessages.add(new Message((String) m));
          } else if (m instanceof Map) {
            Map<String, List<String>> message = (Map<String, List<String>>) m;

            for (Entry<String, List<String>> entry : message.entrySet()) {
              finalMessages.add(new Message(entry.getKey()));
            }
          }
        }
      }

      list.setMessages(finalMessages);

      MessageLists.setList(key, list);
    }

    MessageLists.schedule();

    // Saves any version changes to the disk
    saveConfiguration();

    return true;
  }

  public void saveConfiguration() {
    if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
      saveDefaultConfig();
    }

    for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
      getConfig().set("message-lists." + key, null);
    }

    for (String key : MessageLists.getMessageLists().keySet()) {
      MessageList list = MessageLists.getExactList(key);
      getConfig().set("message-lists." + key + ".enabled", list.isEnabled());
      getConfig().set("message-lists." + key + ".interval", list.getInterval());
      getConfig().set("message-lists." + key + ".expiry", list.getExpiry());
      getConfig().set("message-lists." + key + ".random", list.isRandom());

      List<String> messages = new LinkedList<String>();

      for (Message m : list.getMessages()) {
        messages.add(m.getMessage());
      }

      getConfig().set("message-lists." + key + ".messages", messages);
    }

    saveConfig();
  }

  public File getFile() {
    return super.getFile();
  }

  @Override
  public void onDisable() {
    MessageLists.unschedule();

    plugin = null;
  }

  @Override
  public void onEnable() {
    plugin = this;

    // Setup the base command.
    getCommand("automessage").setExecutor(new BaseCommandExecutor());

    // Register additional commands.
    CommandManager.register(DefaultCommands.class);
    CommandManager.register(PluginCommands.class);

    // Load the configuration.
    if (loadConfig()) {
      // Start metrics.
      try {
        Metrics metrics = new Metrics(this);

        metrics.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
