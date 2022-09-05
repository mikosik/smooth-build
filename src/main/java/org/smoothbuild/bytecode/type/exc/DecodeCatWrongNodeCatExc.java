package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatKindB;

public class DecodeCatWrongNodeCatExc extends DecodeCatNodeExc {
  public DecodeCatWrongNodeCatExc(Hash hash, CatKindB kind, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public DecodeCatWrongNodeCatExc(Hash hash, CatKindB kind, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected category. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
