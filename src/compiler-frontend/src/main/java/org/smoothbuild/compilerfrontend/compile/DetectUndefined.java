package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.ast.define.ScopeP.emptyScope;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.ScopingModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ArrayTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopeP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopedP;
import org.smoothbuild.compilerfrontend.compile.ast.define.TypeP;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

/**
 * Detect undefined referencables and types.
 */
public class DetectUndefined implements TryFunction2<ModuleP, ScopeS, ModuleP> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "detectUndefined");
  }

  @Override
  public Try<ModuleP> apply(ModuleP moduleP, ScopeS imported) {
    var logger = new Logger();
    new Detector(imported, emptyScope(), logger).visitModule(moduleP);
    return Try.of(moduleP, logger);
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
      var name = referenceP.referencedName();
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
      return TypeNamesS.isVarName(name)
          || scope.types().contains(name)
          || imported.types().contains(name);
    }
  }
}
