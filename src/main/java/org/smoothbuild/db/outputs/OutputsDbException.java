package org.smoothbuild.db.outputs;

import java.io.IOException;

import com.google.common.hash.HashCode;

public class OutputsDbException extends RuntimeException {
  public static OutputsDbException corruptedValueException(HashCode hash, String message) {
    return new OutputsDbException(hash.toString() + " value in OutputsDb is corrupted. " + message);
  }

  public static OutputsDbException outputsDbException(IOException e) {
    return new OutputsDbException("IOException when accessing OutputsDb", e);
  }

  public OutputsDbException(String message) {
    super(message);
  }

  public OutputsDbException(String message, Throwable e) {
    super(message, e);
  }
}
