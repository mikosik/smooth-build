package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.ExplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.ImplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopedP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

public class DetectUndefinedReferenceablesAndTypes {
  public static Logs detectUndefinedReferenceablesAndTypes(ModuleP moduleP, ScopeS imported) {
    var log = new LogBuffer();
    var emptyScope = new ScopeP("", immutableBindings(), immutableBindings());
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
    protected ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP) {
      return new Detector(imported, scopedP.scope(), log);
    }

    @Override
    public void visitReference(ReferenceP referenceP) {
      var name = referenceP.name();
      if (!(imported.evaluables().contains(name) || scope.referencables().contains(name))) {
        log.log(compileError(referenceP, Strings.q(name) + " is undefined."));
      }
    }

    @Override
    public void visitType(TypeP typeP) {
      switch (typeP) {
        case ArrayTP array -> visitType(array.elemT());
        case FuncTP func -> visitFuncType(func);
        case ExplicitTP explicitTP -> visitExplicitType(explicitTP);
        case ImplicitTP implicitTP -> {}
      }
    }

    private void visitExplicitType(ExplicitTP explicitTP) {
      if (!isKnownTypeName(explicitTP.name())) {
        log.log(compileError(explicitTP.location(), explicitTP.q() + " type is undefined."));
      }
    }

    private void visitFuncType(FuncTP func) {
      visitType(func.result());
      func.params().forEach(this::visitType);
    }

    private boolean isKnownTypeName(String name) {
      return isVarName(name)
             || scope.types().contains(name)
             || imported.types().contains(name);
    }
  }
}
