package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class MemberImpl implements Member {
  private final BExpr owner;
  private final BExpr expr;
  private final String name;

  public MemberImpl(BExpr owner, BExpr expr, String name) {
    this.owner = owner;
    this.expr = expr;
    this.name = name;
  }

  @Override
  public BExpr asExpr() {
    return expr;
  }

  @Override
  public BExpr asExpr(BType expectedEvaluationType) throws BExprDbException {
    owner.checkMemberEvaluationType(name, expr.evaluationType(), expectedEvaluationType);
    return expr;
  }

  @Override
  public BExpr asExpr(Class<?> expectedEvaluationType) throws BExprDbException {
    owner.checkMemberEvaluationType(name, expr.evaluationType(), expectedEvaluationType);
    return expr;
  }

  @Override
  public <T extends BExpr> T asInstanceOf(Class<T> clazz) throws BExprDbException {
    return owner.castMember(expr, name, clazz);
  }
}
