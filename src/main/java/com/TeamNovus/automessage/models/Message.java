package com.teamnovus.automessage.models;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Message {

  private static final String SPLIT_REGEX = "(?<!\\\\)\\\\n";
  private static final String REPLACE_REGEX = "\\\\\\\\n";
  private static final String REPLACEMENT = "\\\\n";

  private String raw;

  public Message(final String raw) {
    super();
    this.raw = raw;
  }

  public String getMessage() {
    return this.raw;
  }

  public @NotNull Message setMessage(final String raw) {
    this.raw = raw;

    return this;
  }

  public List<String> getMessages() {
    final @NotNull LinkedList<String> messages = new LinkedList<>();

    for (@NotNull String line : this.raw.split(SPLIT_REGEX)) {
      if (!(line.startsWith("/"))) {
        line = line.replaceAll(REPLACE_REGEX, REPLACEMENT);
        messages.add(line);
      }
    }

    return messages;
  }

  public List<String> getCommands() {
    final @NotNull LinkedList<String> commands = new LinkedList<>();

    for (@NotNull String line : this.raw.split(SPLIT_REGEX)) {
      if (line.startsWith("/")) {
        line = line.replaceAll(REPLACE_REGEX, REPLACEMENT);
        commands.add(line);
      }
    }

    return commands;
  }

}
