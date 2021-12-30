package org.smoothbuild.bytecode.obj.exc;


public class DecodeObjExc extends ByteDbExc {
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
