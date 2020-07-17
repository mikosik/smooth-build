package org.smoothbuild.record.db;

import org.smoothbuild.db.hashed.Hash;

public class RecordDbException extends RuntimeException {

  public RecordDbException(Hash hash) {
    this(hash, null, null);
  }

  public RecordDbException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public RecordDbException(Hash hash, String message) {
    this(hash, message, null);
  }

  public RecordDbException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read record at " + hash + "." + (message == null ? "" : " " + message);
  }

  public RecordDbException(Throwable cause) {
    super(cause);
  }
}
