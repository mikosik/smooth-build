package org.smoothbuild.cli;

public class CleanSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Remove all cached values and artifacts calculated during previous builds";
  }

  @Override
  public String longDescription() {
    return "usage: smooth clean\n"
        + "\n"
        + shortDescription();
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Clean.class;
  }
}
