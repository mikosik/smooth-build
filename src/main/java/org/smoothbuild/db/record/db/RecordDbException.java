package org.smoothbuild.db.record.db;

public class RecordDbException extends RuntimeException {
  public RecordDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordDbException(Throwable cause) {
    super(cause);
  }

  public RecordDbException(String message) {
    super(message);
  }
}
