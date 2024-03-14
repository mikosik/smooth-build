package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryId;

public class DecodeCatWrongNodeCatException extends DecodeCatNodeException {
  public DecodeCatWrongNodeCatException(
      Hash hash,
      CategoryId categoryId,
      String memberPath,
      int pathIndex,
      Class<?> expected,
      Class<?> actual) {
    this(hash, categoryId, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public DecodeCatWrongNodeCatException(
      Hash hash, CategoryId categoryId, String path, Class<?> expected, Class<?> actual) {
    super(hash, categoryId, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected category. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
