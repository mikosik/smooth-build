package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;

public class DecodeExprWrongNodeClassException extends DecodeExprNodeException {
  public DecodeExprWrongNodeClassException(
      Hash hash,
      CategoryB cat,
      String memberPath,
      int pathIndex,
      Class<?> expected,
      Class<?> actual) {
    this(hash, cat, indexedPath(memberPath, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeClassException(
      Hash hash, CategoryB cat, String path, Class<?> expected, Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " class but was "
        + actual.getCanonicalName() + " class.";
  }
}
