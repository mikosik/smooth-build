package org.smoothbuild.task;

import com.google.common.hash.HashCode;

@SuppressWarnings("serial")
public class NoTaskWithGivenHashException extends RuntimeException {
  public NoTaskWithGivenHashException(HashCode hash) {
    super("Cannot find task with hash = " + hash);
  }
}
