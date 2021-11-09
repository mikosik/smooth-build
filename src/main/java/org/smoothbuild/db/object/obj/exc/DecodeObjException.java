package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.object.db.ObjectHDbException;

public class DecodeObjException extends ObjectHDbException {
  public DecodeObjException(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeObjException(String message) {
    super(message);
  }
}
