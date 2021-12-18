package org.smoothbuild.db.object.db;

public class ByteDbExc extends RuntimeException {
  public ByteDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public ByteDbExc(Throwable cause) {
    super(cause);
  }

  public ByteDbExc(String message) {
    super(message);
  }
}
