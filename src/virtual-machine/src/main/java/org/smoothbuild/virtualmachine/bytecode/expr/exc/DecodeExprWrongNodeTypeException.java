package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodeExprWrongNodeTypeException extends DecodeExprNodeException {
  public DecodeExprWrongNodeTypeException(
      Hash hash, BKind kind, String path, int pathIndex, BType expected, BType actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BKind kind, String path, BType expected, BType actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(BKind expected, BKind actual) {
    return buildMessage(expected.q(), actual.q());
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BKind kind, String path, BType expected, String actual) {
    super(hash, kind, path, buildMessage(expected.q(), actual));
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BKind kind, String path, int index, Class<?> expected, BType actual) {
    this(hash, kind, indexedPath(path, index), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BKind kind, String path, Class<?> expected, BType actual) {
    super(hash, kind, path, buildMessage("instance of " + expected.getSimpleName(), actual.q()));
  }

  private static String buildMessage(String expected, String actual) {
    return "Node has unexpected type. Expected " + expected + " but was " + actual + ".";
  }
}
