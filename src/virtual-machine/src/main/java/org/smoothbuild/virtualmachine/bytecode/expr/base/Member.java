package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public interface Member {
  public BExpr asExpr();

  public BExpr asExpr(BType expectedEvaluationType) throws BExprDbException;

  public BExpr asExpr(Class<?> expectedEvaluationType) throws BExprDbException;

  public <T extends BExpr> T asInstanceOf(Class<T> clazz) throws BExprDbException;
}
