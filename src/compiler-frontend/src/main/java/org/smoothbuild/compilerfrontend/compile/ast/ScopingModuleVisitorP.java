package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopedP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;

public abstract class ScopingModuleVisitorP extends ModuleVisitorP {
  protected abstract ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP);

  @Override
  public final void visitModule(ModuleP moduleP) {
    createVisitorForScopeOf(moduleP).visitModuleChildren(moduleP);
  }

  @Override
  public final void visitStruct(StructP structP) {
    visitStructSignature(structP);
    createVisitorForScopeOf(structP);
  }

  @Override
  public final void visitNamedValue(NamedValueP namedValueP) {
    visitNamedValueSignature(namedValueP);
    createVisitorForScopeOf(namedValueP).visitNamedValueBody(namedValueP);
  }

  @Override
  public final void visitNamedFunc(NamedFuncP namedFuncP) {
    visitNamedFuncSignature(namedFuncP);
    createVisitorForScopeOf(namedFuncP).visitFuncBody(namedFuncP);
  }

  @Override
  public final void visitLambda(LambdaP lambdaP) {
    visitLambdaSignature(lambdaP);
    createVisitorForScopeOf(lambdaP).visitFuncBody(lambdaP);
  }
}
