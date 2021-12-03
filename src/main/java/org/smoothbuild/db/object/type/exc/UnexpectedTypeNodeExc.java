package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.CatKindH;

public class UnexpectedTypeNodeExc extends DecodeTypeNodeExc {
  public UnexpectedTypeNodeExc(Hash hash, CatKindH kind, String path, int pathIndex,
      CatH expected, CatH actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedTypeNodeExc(Hash hash, CatKindH kind, String path, CatH expected,
      CatH actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatH expected, CatH actual) {
    return "Node has unexpected type. Expected " + expected.name() + " but was " + actual.name()
        + ".";
  }

  public UnexpectedTypeNodeExc(Hash hash, CatKindH kind, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedTypeNodeExc(Hash hash, CatKindH kind, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
