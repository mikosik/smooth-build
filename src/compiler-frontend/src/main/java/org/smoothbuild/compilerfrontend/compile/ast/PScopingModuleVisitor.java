package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;

public class PScopingModuleVisitor<T extends Throwable> extends PModuleVisitor<T> {
  private PScope scope;

  public PScopingModuleVisitor() {
    this.scope = null;
  }

  public PScope scope() {
    return scope;
  }

  @Override
  public void visit(PContainer pContainer) throws T {
    var oldScope = scope;
    try {
      scope = pContainer.scope();
      super.visit(pContainer);
    } finally {
      scope = oldScope;
    }
  }
}
