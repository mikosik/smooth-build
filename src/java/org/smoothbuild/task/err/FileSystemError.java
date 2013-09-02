package org.smoothbuild.task.err;

import org.smoothbuild.problem.Error;

import com.google.common.base.Throwables;

public class FileSystemError extends Error {
  public FileSystemError(Throwable e) {
    super("FileSystem error: " + Throwables.getStackTraceAsString(e));
  }
}
