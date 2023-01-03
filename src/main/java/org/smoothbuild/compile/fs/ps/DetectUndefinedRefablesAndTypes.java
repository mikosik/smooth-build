package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefP;
import org.smoothbuild.compile.fs.ps.ast.expr.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.expr.WithScopeP;
import org.smoothbuild.compile.fs.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.type.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.type.TypeP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

public class DetectUndefinedRefablesAndTypes {
  public static Logs detectUndefinedRefablesAndTypes(ModuleP moduleP, ScopeS imported) {
    var log = new LogBuffer();
    var emptyScope = new ScopeP(immutableBindings(), immutableBindings());
    new Detector(imported, emptyScope, log)
        .visitModule(moduleP);
    return log;
  }

  private static class Detector extends ScopingModuleVisitorP {
    private final ScopeS imported;
    private final ScopeP scope;
    private final Logger log;

    public Detector(ScopeS imported, ScopeP scope, Logger log) {
      this.scope = scope;
      this.imported = imported;
      this.log = log;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(WithScopeP withScopeP) {
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
