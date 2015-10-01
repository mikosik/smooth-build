package org.smoothbuild.cli;

public class HelpSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Print help about given command";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth help <command>\n");
    builder.append("\n");
    builder.append(shortDescription() + "\n");
    builder.append("\n");
    builder.append("arguments:\n");
    builder.append("  <command>  command for which help is printed");
    return builder.toString();
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Help.class;
  }
}
