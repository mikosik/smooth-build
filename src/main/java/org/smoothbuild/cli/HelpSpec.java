package org.smoothbuild.cli;

public class HelpSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Print help about given command";
  }

  @Override
  public String longDescription() {
    return "usage: smooth help <command>\n"
        + "\n"
        + shortDescription() + "\n"
        + "\n"
        + "arguments:\n"
        + "  <command>  command for which help is printed";
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Help.class;
  }
}
