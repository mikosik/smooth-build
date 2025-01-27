package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static org.smoothbuild.common.base.Strings.q;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class MemberHasWrongTypeException extends DecodeExprException {
  public MemberHasWrongTypeException(
      Hash hash, BKind kind, String memberName, Class<?> expected, Class<?> actual) {
    super(buildMessage(hash, kind, memberName, expected.getSimpleName(), actual.getSimpleName()));
  }

  public MemberHasWrongTypeException(
      Hash hash, BKind kind, String memberName, BType expected, BType actual) {
    super(buildMessage(hash, kind, memberName, expected.name(), actual.name()));
  }

  private static String buildMessage(
      Hash hash, BKind kind, String memberName, String expected, String actual) {
    return "Cannot decode " + kind.q() + " expression at " + hash + ". Its " + q(memberName)
        + " has wrong type. Expected " + q(expected) + " but is " + q(actual) + ". ";
  }
}
