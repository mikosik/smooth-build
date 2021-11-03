package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;

public class UnexpectedObjNodeException extends DecodeObjNodeException {
  public UnexpectedObjNodeException(Hash hash, ObjType type, String path, int pathIndex,
      ObjType expected, ObjType actual) {
    this(hash, type, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedObjNodeException(
      Hash hash, ObjType type, String path, ObjType expected, ObjType actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(ObjType expected, ObjType actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public UnexpectedObjNodeException(Hash hash, ObjType type, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, type, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedObjNodeException(Hash hash, ObjType type, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
