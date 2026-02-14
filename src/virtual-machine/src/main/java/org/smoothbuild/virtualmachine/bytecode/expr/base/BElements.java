package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class BElements {
  private final BExpr owner;
  private final List<BExpr> exprs;
  private final String name;

  public BElements(BExpr owner, List<BExpr> exprs, String name) {
    this.owner = owner;
    this.exprs = exprs;
    this.name = name;
  }

  public <T extends BExpr> List<T> asListOfInstancesOf(Class<T> elementType)
      throws BExprDbException {
    checkElementTypes(elementType);
    @SuppressWarnings("unchecked") // safe as List is immutable
    List<T> result = (List<T>) exprs;
    return result;
  }

  public <T extends BExpr> void checkElementTypes(Class<T> elementType) throws BExprDbException {
    for (int i = 0; i < exprs.size(); i++) {
      owner.castSubExpr(exprs.get(i), name + "[" + i + "]", elementType);
    }
  }

  public void checkElementTypes(BType expectedEvaluationType) throws BExprDbException {
    for (int i = 0; i < exprs.size(); i++) {
      owner.checkSubExprEvaluationType(
          name + "[" + i + "]", exprs.get(i).evaluationType(), expectedEvaluationType);
    }
  }

  public List<BExpr> asList() {
    return exprs;
  }
}
