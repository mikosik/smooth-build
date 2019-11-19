package org.smoothbuild.db.outputs;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;

public class OutputsDbException extends Exception {
  public static OutputsDbException corruptedValueException(Hash hash, String message) {
    return new OutputsDbException(hash.toString() + " value in OutputsDb is corrupted. " + message);
  }

  public static OutputsDbException outputsDbException(IOException e) {
    return new OutputsDbException("IOException when accessing OutputsDb", e);
  }

  private OutputsDbException(String message) {
    super(message);
  }

  private OutputsDbException(String message, Throwable e) {
    super(message, e);
  }
}
