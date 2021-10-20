package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.object.db.ObjectDbException;

public class DecodeObjException extends ObjectDbException {
  public DecodeObjException(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeObjException(String message) {
    super(message);
  }
}
