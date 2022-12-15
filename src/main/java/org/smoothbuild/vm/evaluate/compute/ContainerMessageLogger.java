package org.smoothbuild.vm.evaluate.compute;

import org.smoothbuild.vm.evaluate.plugin.MessageLogger;

public interface ContainerMessageLogger extends MessageLogger {
  public void fatal(String message);
}
