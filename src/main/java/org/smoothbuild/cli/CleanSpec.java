package org.smoothbuild.cli;

public class CleanSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Remove all cached values and artifacts calculated during previous builds";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth clean\n");
    builder.append("\n");
    builder.append(shortDescription());
    return builder.toString();
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Clean.class;
  }
}
