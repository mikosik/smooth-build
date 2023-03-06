package org.smoothbuild.vm.evaluate.plugin;

public interface MessageLogger {
  public void error(String message);

  public void warning(String message);

  public void info(String message);
}
