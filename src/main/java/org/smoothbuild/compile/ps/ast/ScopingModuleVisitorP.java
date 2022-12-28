package org.smoothbuild.compile.ps.ast;

import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.expr.WithScopeP;

public abstract class ScopingModuleVisitorP extends ModuleVisitorP {
  protected abstract ModuleVisitorP createVisitorForScopeOf(WithScopeP withScopeP);

  @Override
  public final void visitModule(ModuleP moduleP) {
    createVisitorForScopeOf(moduleP)
        .visitModuleChildren(moduleP);
  }

  @Override
  public final void visitStruct(StructP structP) {
    visitStructSignature(structP);
  }

  @Override
  public final void visitNamedValue(NamedValueP namedValueP) {
    visitNamedValueSignature(namedValueP);
    createVisitorForScopeOf(namedValueP)
        .visitNamedValueBody(namedValueP);
  }

  @Override
  public final void visitNamedFunc(NamedFuncP namedFuncP) {
    visitNamedFuncSignature(namedFuncP);
    createVisitorForScopeOf(namedFuncP)
        .visitFuncBody(namedFuncP);
  }

  @Override
  public final void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    visitAnonymousFuncSignature(anonymousFuncP);
    createVisitorForScopeOf(anonymousFuncP)
        .visitFuncBody(anonymousFuncP);
  }
}
