package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;

public abstract class PScopingModuleVisitor<T extends Throwable> extends PModuleVisitor<T> {
  protected abstract PModuleVisitor<T> createVisitorForScopeOf(PContainer pContainer);

  @Override
  public final void visitModule(PModule pModule) throws T {
    createVisitorForScopeOf(pModule).visitModuleChildren(pModule);
  }

  @Override
  public final void visitStruct(PStruct pStruct) throws T {
    visitStructSignature(pStruct);
    createVisitorForScopeOf(pStruct);
  }

  @Override
  public final void visitNamedValue(PNamedValue pNamedValue) throws T {
    visitNamedValueSignature(pNamedValue);
    createVisitorForScopeOf(pNamedValue).visitNamedValueBody(pNamedValue);
  }

  @Override
  public final void visitNamedFunc(PNamedFunc pNamedFunc) throws T {
    visitNamedFuncSignature(pNamedFunc);
    createVisitorForScopeOf(pNamedFunc).visitFuncBody(pNamedFunc);
  }

  @Override
  public final void visitLambda(PLambda pLambda) throws T {
    visitLambdaSignature(pLambda);
    createVisitorForScopeOf(pLambda).visitFuncBody(pLambda);
  }
}
