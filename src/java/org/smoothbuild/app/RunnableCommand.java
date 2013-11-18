package org.smoothbuild.app;

public interface RunnableCommand {
  /**
   * @return true on success on false when problems occurred
   */
  public boolean runCommand();
}
