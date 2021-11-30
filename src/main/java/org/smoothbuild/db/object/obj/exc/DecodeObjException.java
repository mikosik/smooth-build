package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.object.db.ObjDbException;

public class DecodeObjException extends ObjDbException {
  public DecodeObjException(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeObjException(String message) {
    super(message);
  }
}
