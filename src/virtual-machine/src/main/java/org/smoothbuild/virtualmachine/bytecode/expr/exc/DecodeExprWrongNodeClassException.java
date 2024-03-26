package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.base.BKind;

public class DecodeExprWrongNodeClassException extends DecodeExprNodeException {
  public DecodeExprWrongNodeClassException(
      Hash hash, BKind kind, String memberPath, int pathIndex, Class<?> expected, Class<?> actual) {
    this(hash, kind, indexedPath(memberPath, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeClassException(
      Hash hash, BKind kind, String path, Class<?> expected, Class<?> actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " class but was "
        + actual.getCanonicalName() + " class.";
  }
}
