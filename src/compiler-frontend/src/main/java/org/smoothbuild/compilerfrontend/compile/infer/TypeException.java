package org.smoothbuild.compilerfrontend.compile.infer;

import org.smoothbuild.common.log.base.Log;

public class TypeException extends Exception {
  private final Log log;

  public TypeException(Log log, Exception cause) {
    super(cause);
    this.log = log;
  }

  public TypeException(Log log) {
    this.log = log;
  }

  public Log log() {
    return log;
  }
}
