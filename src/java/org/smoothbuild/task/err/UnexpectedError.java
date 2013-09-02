package org.smoothbuild.task.err;

import org.smoothbuild.problem.Error;

import com.google.common.base.Throwables;

public class UnexpectedError extends Error {
  public UnexpectedError(Throwable e) {
    super("Unexpected error: " + Throwables.getStackTraceAsString(e));
  }
}
