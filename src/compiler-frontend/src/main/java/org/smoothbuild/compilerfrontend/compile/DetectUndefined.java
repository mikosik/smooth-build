package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.ast.define.PScope.emptyScope;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScoped;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.STypeNames;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

/**
 * Detect undefined referencables and types.
 */
public class DetectUndefined implements Task2<PModule, SScope, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule, SScope imported) {
    var logger = new Logger();
    new Detector(imported, emptyScope(), logger).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":detectUndefined");
    return output(pModule, label, logger.toList());
  }

  private static class Detector extends PScopingModuleVisitor<RuntimeException> {
    private final SScope imported;
    private final PScope scope;
    private final Logger log;

    public Detector(SScope imported, PScope scope, Logger log) {
      this.scope = scope;
      this.imported = imported;
      this.log = log;
    }

    @Override
    protected PModuleVisitor createVisitorForScopeOf(PScoped pScoped) {
      return new Detector(imported, pScoped.scope(), log);
    }

    @Override
    public void visitReference(PReference pReference) {
      var name = pReference.referencedName();
      if (!(imported.evaluables().contains(name) || scope.referencables().contains(name))) {
        log.log(compileError(pReference, Strings.q(name) + " is undefined."));
      }
    }

    @Override
    public void visitType(PType pType) {
      switch (pType) {
        case PArrayType array -> visitType(array.elemT());
        case PFuncType func -> visitFuncType(func);
        case PExplicitType pExplicitType -> visitExplicitType(pExplicitType);
        case PImplicitType pImplicitType -> {}
      }
    }

    private void visitExplicitType(PExplicitType pExplicitType) {
      if (!isKnownTypeName(pExplicitType.name())) {
        log.log(compileError(pExplicitType.location(), pExplicitType.q() + " type is undefined."));
      }
    }

    private void visitFuncType(PFuncType func) {
      visitType(func.result());
      func.params().forEach(this::visitType);
    }

    private boolean isKnownTypeName(String name) {
      return STypeNames.isVarName(name)
          || scope.types().contains(name)
          || imported.types().contains(name);
    }
  }
}
