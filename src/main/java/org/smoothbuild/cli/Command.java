package org.smoothbuild.cli;

public interface Command {
  public String shortDescription();

  public String longDescription();

  public int execute(String[] args);
}
