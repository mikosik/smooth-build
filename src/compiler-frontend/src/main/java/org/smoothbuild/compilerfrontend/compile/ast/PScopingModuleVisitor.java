package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;

public class PScopingModuleVisitor<T extends Throwable> extends PModuleVisitor<PScope, T> {
  protected PScope scope() {
    return containerProperty();
  }

  @Override
  protected PScope propertyOf(PContainer pContainer) {
    return pContainer.scope();
  }
}
