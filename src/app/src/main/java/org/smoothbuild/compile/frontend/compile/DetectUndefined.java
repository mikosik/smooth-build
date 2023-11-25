package org.smoothbuild.compile.frontend.compile;

import static org.smoothbuild.compile.frontend.compile.CompileError.compileError;
import static org.smoothbuild.compile.frontend.compile.ast.define.ScopeP.emptyScope;
import static org.smoothbuild.compile.frontend.lang.base.TypeNamesS.isVarName;

import io.vavr.Tuple2;
import java.util.function.Function;
import org.smoothbuild.common.Strings;
import org.smoothbuild.compile.frontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compile.frontend.compile.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.frontend.compile.ast.define.ArrayTP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExplicitTP;
import org.smoothbuild.compile.frontend.compile.ast.define.FuncTP;
import org.smoothbuild.compile.frontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compile.frontend.compile.ast.define.ModuleP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.ScopeP;
import org.smoothbuild.compile.frontend.compile.ast.define.ScopedP;
import org.smoothbuild.compile.frontend.compile.ast.define.TypeP;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Try;

/**
 * Detect undefined referencables and types.
 */
public class DetectUndefined implements Function<Tuple2<ModuleP, ScopeS>, Try<ModuleP>> {
  @Override
  public Try<ModuleP> apply(Tuple2<ModuleP, ScopeS> context) {
    var logBuffer = new LogBuffer();
    var moduleP = context._1();
    new Detector(context._2(), emptyScope(), logBuffer).visitModule(moduleP);
    return Try.of(moduleP, logBuffer);
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
      return isVarName(name) || scope.types().contains(name) || imported.types().contains(name);
    }
  }
}
