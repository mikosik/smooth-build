package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjKind;
import org.smoothbuild.db.object.type.base.TypeO;

public class UnexpectedTypeNodeException extends DecodeTypeNodeException {
  public UnexpectedTypeNodeException(Hash hash, ObjKind kind, String path, int pathIndex,
      TypeO expected, TypeO actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedTypeNodeException(Hash hash, ObjKind kind, String path, TypeO expected,
      TypeO actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(TypeO expected, TypeO actual) {
    return "Node has unexpected type. Expected " + expected.name() + " but was " + actual.name()
        + ".";
  }

  public UnexpectedTypeNodeException(Hash hash, ObjKind kind, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedTypeNodeException(Hash hash, ObjKind kind, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
