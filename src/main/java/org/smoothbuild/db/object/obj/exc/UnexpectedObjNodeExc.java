package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecH;

public class UnexpectedObjNodeExc extends DecodeObjNodeExc {
  public UnexpectedObjNodeExc(Hash hash, SpecH type, String path, int pathIndex,
      SpecH expected, SpecH actual) {
    this(hash, type, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedObjNodeExc(
      Hash hash, SpecH type, String path, SpecH expected, SpecH actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(SpecH expected, SpecH actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public UnexpectedObjNodeExc(Hash hash, SpecH type, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, type, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedObjNodeExc(Hash hash, SpecH type, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, type, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}
