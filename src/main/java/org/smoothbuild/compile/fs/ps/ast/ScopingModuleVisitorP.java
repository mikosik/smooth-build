package org.smoothbuild.compile.fs.ps.ast;

import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.WithScopeP;

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
