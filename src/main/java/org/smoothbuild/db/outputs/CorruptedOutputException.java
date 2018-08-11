package org.smoothbuild.db.outputs;

import com.google.common.hash.HashCode;

public class CorruptedOutputException extends RuntimeException {
  public CorruptedOutputException(HashCode hash, String message) {
    super(hash.toString() + " value in OutputsDb is corrupted. " + message);
  }
}
