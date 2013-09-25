package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;

@SuppressWarnings("serial")
public class ReflexiveInternalError extends PluginInternalError {
  public ReflexiveInternalError(Name name, Throwable e) {
    super(name, e);
  }
}
