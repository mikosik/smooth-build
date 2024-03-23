package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodeExprWrongNodeTypeException extends DecodeExprNodeException {
  public DecodeExprWrongNodeTypeException(
      Hash hash, BCategory cat, String path, int pathIndex, BType expected, BType actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BCategory cat, String path, BType expected, BType actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(BCategory expected, BCategory actual) {
    return buildMessage(expected.q(), actual.q());
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BCategory cat, String path, BType expected, String actual) {
    super(hash, cat, path, buildMessage(expected.q(), actual));
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BCategory cat, String path, int index, Class<?> expected, BType actual) {
    this(hash, cat, indexedPath(path, index), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, BCategory cat, String path, Class<?> expected, BType actual) {
    super(hash, cat, path, buildMessage("instance of " + expected.getSimpleName(), actual.q()));
  }

  private static String buildMessage(String expected, String actual) {
    return "Node has unexpected type. Expected " + expected + " but was " + actual + ".";
  }
}
