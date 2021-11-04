package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;

public class UnexpectedObjNodeException extends DecodeObjNodeException {
  public UnexpectedObjNodeException(Hash hash, TypeO type, String path, int pathIndex,
      TypeO expected, TypeO actual) {
    this(hash, type, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedObjNodeException(
      Hash hash, TypeO type, String path, TypeO expected, TypeO actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(TypeO expected, TypeO actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public UnexpectedObjNodeException(Hash hash, TypeO type, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, type, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedObjNodeException(Hash hash, TypeO type, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
