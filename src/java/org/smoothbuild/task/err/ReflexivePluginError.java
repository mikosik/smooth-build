package org.smoothbuild.task.err;

import org.smoothbuild.problem.Error;

import com.google.common.base.Throwables;

public class ReflexivePluginError extends Error {
  public ReflexivePluginError(Throwable e) {
    super("Plugin invocation caused exception:\n" + Throwables.getStackTraceAsString(e));
  }
}
