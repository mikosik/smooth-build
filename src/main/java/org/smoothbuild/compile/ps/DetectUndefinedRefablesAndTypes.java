package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.base.ValidNamesS.isVarName;
import static org.smoothbuild.compile.ps.CompileError.compileError;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.ScopeP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.bindings.MutableBindings;

public class DetectUndefinedRefablesAndTypes {
  public static Logs detectUndefinedRefablesAndTypes(ModuleP moduleP, DefinitionsS imported) {
    var log = new LogBuffer();
    var emptyScope = new ScopeP(new MutableBindings<>(), new MutableBindings<>());
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
      var detector = new Detector(imported, moduleP.scope(), log);
      detector.visitStructs(moduleP.structs());
      detector.visitNamedEvaluables(moduleP.evaluables());
    }

    @Override
    public void visitStruct(StructP structP) {
      super.visitStruct(structP);
    }

    @Override
    public void visitNamedEvaluable(NamedEvaluableP namedEvaluableP) {
      super.visitNamedEvaluable(namedEvaluableP);
    }

    @Override
    public void visitFuncBody(FuncP funcP, ExprP exprP) {
      new Detector(imported, funcP.scope(), log)
          .visitExpr(exprP);
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
