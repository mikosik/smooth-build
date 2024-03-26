package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static org.smoothbuild.common.base.Strings.q;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

public class DecodeExprWrongMemberEvaluationTypeException extends DecodeExprException {
  public DecodeExprWrongMemberEvaluationTypeException(
      Hash hash, BKind kind, String memberName, BType expected, BType actual) {
    this(hash, kind, memberName, expected.q(), actual);
  }

  public DecodeExprWrongMemberEvaluationTypeException(
      Hash hash, BKind kind, String memberName, String expected, BType actual) {
    super(buildMessage(hash, kind, q(memberName), expected, actual));
  }

  private static String buildMessage(
      Hash hash, BKind kind, String memberName, String expected, BType actual) {
    return "Cannot decode " + kind.q() + " expression at " + hash + ". Its `" + memberName
        + "` has wrong evaluation type. Expected " + expected + " but is " + actual.q()
        + ". ";
  }
}
