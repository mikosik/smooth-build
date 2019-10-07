package org.smoothbuild.db.hashed;

import java.io.IOException;

import com.google.common.hash.HashCode;

public class NoSuchDataException extends IOException {
  public NoSuchDataException(HashCode hash) {
    super("No data at " + hash + ".");
  }
}
