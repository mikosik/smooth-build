package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;

public class UnexpectedError extends PluginInternalError {
  public UnexpectedError(Name name, Throwable e) {
    super(name, e);
  }
}
