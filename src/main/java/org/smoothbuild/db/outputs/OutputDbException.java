package org.smoothbuild.db.outputs;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;

public class OutputDbException extends Exception {
  public static OutputDbException corruptedValueException(Hash hash, String message) {
    return new OutputDbException(hash.toString() + " value in OutputDb is corrupted. " + message);
  }

  public static OutputDbException outputDbException(IOException e) {
    return new OutputDbException("IOException when accessing OutputDb", e);
  }

  private OutputDbException(String message) {
    super(message);
  }

  private OutputDbException(String message, Throwable e) {
    super(message, e);
  }
}
