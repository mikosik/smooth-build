package org.smoothbuild.vm.compute;

import org.smoothbuild.plugin.MessageLogger;

public interface ContainerMessageLogger extends MessageLogger {
  public void fatal(String message);
}
