package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class NodeHasWrongTypeException extends DecodeExprNodeException {
  public NodeHasWrongTypeException(
      Hash hash, BKind kind, String path, int pathIndex, BType expected, BType actual) {
    this(hash, kind, indexedPath(path, pathIndex), expected, actual);
  }

  public NodeHasWrongTypeException(
      Hash hash, BKind kind, String path, BType expected, BType actual) {
    super(hash, kind, path, buildMessage(expected, actual));
  }

  private static String buildMessage(BKind expected, BKind actual) {
    return buildMessage(expected.q(), actual.q());
  }

  private static String buildMessage(String expected, String actual) {
    return "Node has unexpected type. Expected " + expected + " but was " + actual + ".";
  }
}
