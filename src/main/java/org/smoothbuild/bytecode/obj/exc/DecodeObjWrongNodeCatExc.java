package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;

public class DecodeObjWrongNodeCatExc extends DecodeObjNodeExc {
  public DecodeObjWrongNodeCatExc(Hash hash, CatB cat, String path, int pathIndex,
      CatB expected, CatB actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeObjWrongNodeCatExc(
      Hash hash, CatB cat, String path, CatB expected, CatB actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatB expected, CatB actual) {
    return "Node has unexpected category. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public DecodeObjWrongNodeCatExc(Hash hash, CatB cat, String path, int index,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(path, index), expected, actual);
  }

  public DecodeObjWrongNodeCatExc(Hash hash, CatB cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected category. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
