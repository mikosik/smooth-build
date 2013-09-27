package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Error;

import com.google.common.base.Throwables;

@SuppressWarnings("serial")
public class InvocationError extends Error {
  public InvocationError(Name name, Throwable e) {
    super("Invoking function " + name + " caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
