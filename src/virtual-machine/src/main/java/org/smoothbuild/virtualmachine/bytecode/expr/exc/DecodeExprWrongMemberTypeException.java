package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static org.smoothbuild.common.base.Strings.q;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class DecodeExprWrongMemberTypeException extends DecodeExprException {
  public DecodeExprWrongMemberTypeException(
      Hash hash, BKind kind, String memberName, Class<?> expected, Class<?> actual) {
    super(buildMessage(hash, kind, memberName, expected, actual));
  }

  private static String buildMessage(
      Hash hash, BKind kind, String memberName, Class<?> expected, Class<?> actual) {
    return "Cannot decode " + kind.q() + " expression at " + hash + ". Its " + q(memberName)
        + " has wrong type. Expected " + q(expected.getSimpleName()) + " but is "
        + q(actual.getSimpleName()) + ". ";
  }
}
