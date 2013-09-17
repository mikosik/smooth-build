package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.Error;

import com.google.common.base.Throwables;

public class PluginInternalError extends Error {
  public PluginInternalError(Name name, Throwable e) {
    super("Invoking function " + name + " caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
