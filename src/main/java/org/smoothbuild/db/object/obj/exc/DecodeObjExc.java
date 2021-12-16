package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.object.db.ObjDbExc;

public class DecodeObjExc extends ObjDbExc {
  public DecodeObjExc(String message, Throwable cause) {
    super(message, cause);
  }

  public DecodeObjExc(String message) {
    super(message);
  }

  public static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }
}
