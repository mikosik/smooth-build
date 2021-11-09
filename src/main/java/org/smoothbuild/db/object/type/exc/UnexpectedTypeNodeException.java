package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeKindH;

public class UnexpectedTypeNodeException extends DecodeTypeNodeException {
  public UnexpectedTypeNodeException(Hash hash, TypeKindH kind, String path, int pathIndex,
      TypeH expected, TypeH actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedTypeNodeException(Hash hash, TypeKindH kind, String path, TypeH expected,
      TypeH actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(TypeH expected, TypeH actual) {
    return "Node has unexpected type. Expected " + expected.name() + " but was " + actual.name()
        + ".";
  }

  public UnexpectedTypeNodeException(Hash hash, TypeKindH kind, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedTypeNodeException(Hash hash, TypeKindH kind, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
