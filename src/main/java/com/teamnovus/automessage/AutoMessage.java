package com.teamnovus.automessage;

import com.teamnovus.automessage.commands.DefaultCommands;
import com.teamnovus.automessage.models.Message;
import com.teamnovus.automessage.models.MessageList;
import com.teamnovus.automessage.models.MessageLists;
import com.teamnovus.automessage.commands.PluginCommands;
import com.teamnovus.automessage.commands.common.BaseCommandExecutor;
import com.teamnovus.automessage.commands.common.CommandManager;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoMessage extends JavaPlugin {

  public static @NotNull AutoMessage plugin;

  public void loadConfig() {
    if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
      saveDefaultConfig();
    }

    try {
      new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
    } catch (final Exception e) {
      getLogger().log(Level.SEVERE, "--- --- --- ---");
      getLogger().log(Level.SEVERE, "There was an error loading your configuration.");
      getLogger().log(Level.SEVERE, "A detailed description of your error is shown below.");
      getLogger().log(Level.SEVERE, "--- --- --- ---");
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);

      return;
    }

    reloadConfig();

    MessageLists.clear();

    for (final String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
      final @NotNull MessageList list = new MessageList();

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

      final @NotNull LinkedList<Message> finalMessages = new LinkedList<>();

      if (getConfig().contains("message-lists." + key + ".messages")) {
        final @Nullable List<Object> messages = (List<Object>) getConfig().getList(
            "message-lists." + key + ".messages");

        for (final Object m : messages) {
          if (m instanceof String) {
            finalMessages.add(new Message((String) m));
          } else if (m instanceof Map) {
            final @NotNull Map<String, List<String>> message = (Map<String, List<String>>) m;

            for (final @NotNull Entry<String, List<String>> entry : message.entrySet()) {
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

  }

  public void saveConfiguration() {
    if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
      saveDefaultConfig();
    }

    for (final String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
      getConfig().set("message-lists." + key, null);
    }

    for (final String key : MessageLists.getMessageLists().keySet()) {
      final MessageList list = MessageLists.getExactList(key);
      getConfig().set("message-lists." + key + ".enabled", list.isEnabled());
      getConfig().set("message-lists." + key + ".interval", list.getInterval());
      getConfig().set("message-lists." + key + ".expiry", list.getExpiry());
      getConfig().set("message-lists." + key + ".random", list.isRandom());

      final @NotNull List<String> messages = new LinkedList<>();

      for (final @NotNull Message message : list.getMessages()) {
        messages.add(message.getMessage());
      }

      getConfig().set("message-lists." + key + ".messages", messages);
    }

    saveConfig();
  }

  @Override
  public @NotNull File getFile() {
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

    // Set up the base command.
    getCommand("automessage").setExecutor(new BaseCommandExecutor());

    // Register additional commands.
    CommandManager.register(DefaultCommands.class);
    CommandManager.register(PluginCommands.class);

    // Load the configuration.
    loadConfig();
  }
}
