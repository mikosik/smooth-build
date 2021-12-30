package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

public class DecodeObjWrongNodeTypeExc extends DecodeObjNodeExc {
  public DecodeObjWrongNodeTypeExc(Hash hash, CatB cat, String path, int pathIndex,
      TypeB expected, TypeB actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeObjWrongNodeTypeExc(
      Hash hash, CatB cat, String path, TypeB expected, TypeB actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatB expected, CatB actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }

  public DecodeObjWrongNodeTypeExc(Hash hash, CatB cat, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(memberPath, pathIndex), expected, actual);
  }

  public DecodeObjWrongNodeTypeExc(Hash hash, CatB cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected type. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
