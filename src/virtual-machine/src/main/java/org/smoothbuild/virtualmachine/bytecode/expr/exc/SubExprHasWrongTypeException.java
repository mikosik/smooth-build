package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static org.smoothbuild.common.base.Strings.q;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class SubExprHasWrongTypeException extends DecodeExprException {
  public SubExprHasWrongTypeException(
      Hash hash, BKind kind, String subExprName, Class<?> expected, Class<?> actual) {
    super(buildMessage(hash, kind, subExprName, expected.getSimpleName(), actual.getSimpleName()));
  }

  public SubExprHasWrongTypeException(
      Hash hash, BKind kind, String subExprName, BType expected, BType actual) {
    super(buildMessage(hash, kind, subExprName, expected.name(), actual.name()));
  }

  private static String buildMessage(
      Hash hash, BKind kind, String subExprName, String expected, String actual) {
    return "Cannot decode " + kind.q() + " expression at " + hash + ". Its " + q(subExprName)
        + " has wrong type. Expected " + q(expected) + " but is " + q(actual) + ". ";
  }
}
