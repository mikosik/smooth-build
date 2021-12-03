package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;

public class UnexpectedObjNodeExc extends DecodeObjNodeExc {
  public UnexpectedObjNodeExc(Hash hash, CatH cat, String path, int pathIndex,
      CatH expected, CatH actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedObjNodeExc(
      Hash hash, CatH cat, String path, CatH expected, CatH actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatH expected, CatH actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public UnexpectedObjNodeExc(Hash hash, CatH cat, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedObjNodeExc(Hash hash, CatH cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
