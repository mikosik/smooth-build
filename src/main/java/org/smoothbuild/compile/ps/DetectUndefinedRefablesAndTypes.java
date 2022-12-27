package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.bindings.Bindings.mutableBindings;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.ScopeP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.expr.WithScopeP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

public class DetectUndefinedRefablesAndTypes {
  public static Logs detectUndefinedRefablesAndTypes(ModuleP moduleP, DefinitionsS imported) {
    var log = new LogBuffer();
    var emptyScope = new ScopeP(mutableBindings(), mutableBindings());
    new Detector(imported, emptyScope, log)
        .visitModule(moduleP);
    return log;
  }

  private static class Detector extends ModuleVisitorP {
    private final DefinitionsS imported;
    private final ScopeP scope;
    private final Logger log;

    public Detector(DefinitionsS imported, ScopeP scope, Logger log) {
      this.scope = scope;
      this.imported = imported;
      this.log = log;
    }

    @Override
    public void visitModule(ModuleP moduleP) {
      newDetector(moduleP)
          .visitModuleChildren(moduleP);
    }

    @Override
    public void visitStruct(StructP structP) {
      newDetector(structP)
          .visitItems(structP.fields());
    }

    @Override
    public void visitNamedValue(NamedValueP namedValueP) {
      visitNamedValueSignature(namedValueP);
      newDetector(namedValueP)
          .visitNamedValueBody(namedValueP);
    }

    @Override
    public void visitFuncBody(FuncP funcP, ExprP exprP) {
      newDetector(funcP)
          .visitExpr(exprP);
    }

    private Detector newDetector(WithScopeP withScopeP) {
      return new Detector(imported, withScopeP.scope(), log);
    }

    @Override
    public void visitRef(RefP refP) {
      var name = refP.name();
      if (!(imported.evaluables().contains(name) || scope.refables().contains(name))) {
        log.log(compileError(refP, Strings.q(name) + " is undefined."));
      }
    }

    @Override
    public void visitType(TypeP typeP) {
      if (typeP instanceof ArrayTP array) {
        visitType(array.elemT());
      } else if (typeP instanceof FuncTP func) {
        visitType(func.resT());
        func.paramTs().forEach(this::visitType);
      } else if (!isDefinedType(typeP)) {
        log.log(compileError(typeP.location(), typeP.q() + " type is undefined."));
      }
    }

    private boolean isDefinedType(TypeP type) {
      var name = type.name();
      return isVarName(name)
             || scope.types().contains(name)
             || imported.types().contains(name);
    }
  }
}
