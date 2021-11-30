package org.smoothbuild.db.object.db;

public class ObjDbExc extends RuntimeException {
  public ObjDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjDbExc(Throwable cause) {
    super(cause);
  }

  public ObjDbExc(String message) {
    super(message);
  }
}
