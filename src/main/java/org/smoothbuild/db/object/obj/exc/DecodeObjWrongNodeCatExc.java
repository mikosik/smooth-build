package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;

public class DecodeObjWrongNodeCatExc extends DecodeObjNodeExc {
  public DecodeObjWrongNodeCatExc(Hash hash, CatH cat, String path, int pathIndex,
      CatH expected, CatH actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeObjWrongNodeCatExc(
      Hash hash, CatH cat, String path, CatH expected, CatH actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatH expected, CatH actual) {
    return "Node has unexpected category. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public DecodeObjWrongNodeCatExc(Hash hash, CatH cat, String path, int index,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(path, index), expected, actual);
  }

  public DecodeObjWrongNodeCatExc(Hash hash, CatH cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected category. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
