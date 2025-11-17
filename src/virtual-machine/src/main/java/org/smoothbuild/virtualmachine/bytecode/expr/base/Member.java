package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class Member {
  private final BExpr owner;
  private final BExpr expr;
  private final String name;

  public Member(BExpr owner, BExpr expr, String name) {
    this.owner = owner;
    this.expr = expr;
    this.name = name;
  }

  public BExpr asExpr() {
    return expr;
  }

  public BExpr asExpr(BType expectedEvaluationType) throws BExprDbException {
    assertEvaluationType(expectedEvaluationType);
    return expr;
  }

  public BExpr asExpr(Class<?> expectedEvaluationType) throws BExprDbException {
    assertEvaluationType(expectedEvaluationType);
    return expr;
  }

  public void assertEvaluationType(BType expectedEvaluationType) throws BExprDbException {
    owner.checkMemberEvaluationType(name, expr.evaluationType(), expectedEvaluationType);
  }

  public void assertEvaluationType(Class<?> expectedEvaluationType) throws BExprDbException {
    owner.checkMemberEvaluationType(name, expr.evaluationType(), expectedEvaluationType);
  }

  public <T extends BExpr> T asInstanceOf(Class<T> clazz) throws BExprDbException {
    return owner.castMember(expr, name, clazz);
  }
}
