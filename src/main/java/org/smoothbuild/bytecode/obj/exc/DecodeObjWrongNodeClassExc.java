package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.db.Hash;

public class DecodeObjWrongNodeClassExc extends DecodeObjNodeExc {
  public DecodeObjWrongNodeClassExc(Hash hash, CatB cat, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(memberPath, pathIndex), expected, actual);
  }

  public DecodeObjWrongNodeClassExc(Hash hash, CatB cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
