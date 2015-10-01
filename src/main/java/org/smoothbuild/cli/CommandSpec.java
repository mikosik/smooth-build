package org.smoothbuild.cli;

public interface CommandSpec {
  public String shortDescription();

  public String longDescription();

  public Class<? extends Command> commandClass();
}
