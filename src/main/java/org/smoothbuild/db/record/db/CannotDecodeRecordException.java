package org.smoothbuild.db.record.db;

import org.smoothbuild.db.hashed.Hash;

public class CannotDecodeRecordException extends RecordDbException {
  public CannotDecodeRecordException(Hash hash) {
    this(hash, null, null);
  }

  public CannotDecodeRecordException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public CannotDecodeRecordException(Hash hash, String message) {
    this(hash, message, null);
  }

  public CannotDecodeRecordException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read record at " + hash + "." + (message == null ? "" : " " + message);
  }

  public CannotDecodeRecordException(Throwable cause) {
    super(cause);
  }
}
