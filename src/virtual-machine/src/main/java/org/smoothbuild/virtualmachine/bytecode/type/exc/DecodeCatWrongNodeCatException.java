package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;

public class DecodeCatWrongNodeCatException extends DecodeCatNodeException {
  public DecodeCatWrongNodeCatException(
      Hash hash,
      CategoryKindB kind,
      String memberPath,
      int pathIndex,
      Class<?> expected,
      Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public DecodeCatWrongNodeCatException(
      Hash hash, CategoryKindB kind, String path, Class<?> expected, Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected category. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
