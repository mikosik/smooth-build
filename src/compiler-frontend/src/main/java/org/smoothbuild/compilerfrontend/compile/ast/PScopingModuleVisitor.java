package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScoped;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;

public abstract class PScopingModuleVisitor extends PModuleVisitor {
  protected abstract PModuleVisitor createVisitorForScopeOf(PScoped pScoped);

  @Override
  public final void visitModule(PModule pModule) {
    createVisitorForScopeOf(pModule).visitModuleChildren(pModule);
  }

  @Override
  public final void visitStruct(PStruct pStruct) {
    visitStructSignature(pStruct);
    createVisitorForScopeOf(pStruct);
  }

  @Override
  public final void visitNamedValue(PNamedValue pNamedValue) {
    visitNamedValueSignature(pNamedValue);
    createVisitorForScopeOf(pNamedValue).visitNamedValueBody(pNamedValue);
  }

  @Override
  public final void visitNamedFunc(PNamedFunc pNamedFunc) {
    visitNamedFuncSignature(pNamedFunc);
    createVisitorForScopeOf(pNamedFunc).visitFuncBody(pNamedFunc);
  }

  @Override
  public final void visitLambda(PLambda pLambda) {
    visitLambdaSignature(pLambda);
    createVisitorForScopeOf(pLambda).visitFuncBody(pLambda);
  }
}
