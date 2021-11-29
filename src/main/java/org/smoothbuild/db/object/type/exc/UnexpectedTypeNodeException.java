package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.SpecKindH;

public class UnexpectedTypeNodeException extends DecodeTypeNodeException {
  public UnexpectedTypeNodeException(Hash hash, SpecKindH kind, String path, int pathIndex,
      SpecH expected, SpecH actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedTypeNodeException(Hash hash, SpecKindH kind, String path, SpecH expected,
      SpecH actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(SpecH expected, SpecH actual) {
    return "Node has unexpected type. Expected " + expected.name() + " but was " + actual.name()
        + ".";
  }

  public UnexpectedTypeNodeException(Hash hash, SpecKindH kind, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedTypeNodeException(Hash hash, SpecKindH kind, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
