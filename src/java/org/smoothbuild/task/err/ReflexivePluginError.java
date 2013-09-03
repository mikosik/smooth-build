package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.Error;

import com.google.common.base.Throwables;

public class ReflexivePluginError extends Error {
  public ReflexivePluginError(Name name, Throwable e) {
    super("Invoking function " + name + " caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
