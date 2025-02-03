package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.ast.define.PScope.emptyScope;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.isTypeVarName;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeReference;

/**
 * Detect undefined referenceables and types.
 */
public class DetectUndefined implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    new Detector(emptyScope(), logger).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":detectUndefined");
    return output(pModule, label, logger.toList());
  }

  private static class Detector extends PScopingModuleVisitor<RuntimeException> {
    private final PScope scope;
    private final Logger log;

    public Detector(PScope scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    @Override
    public void visitNamedEvaluables(List<PNamedEvaluable> pNamedEvaluables) {
      for (var pNamedEvaluable : pNamedEvaluables) {
        // Do not check generated constructor as any problem it can have is caused by problem
        // in its struct which is reported separately.
        if (!(pNamedEvaluable instanceof PConstructor)) {
          visitNamedEvaluable(pNamedEvaluable);
        }
      }
    }

    @Override
    protected PModuleVisitor<RuntimeException> createVisitorForScopeOf(PContainer pContainer) {
      return new Detector(pContainer.scope(), log);
    }

    @Override
    public void visitReference(PReference pReference) {
      var id = pReference.id();
      scope.referencables().find(id).ifErr(e -> log.log(compileError(pReference, e)));
    }

    @Override
    public void visitType(PType pType) {
      switch (pType) {
        case PArrayType array -> visitType(array.elemT());
        case PFuncType func -> visitFuncType(func);
        case PTypeReference pTypeReference -> visitExplicitType(pTypeReference);
        case PImplicitType pImplicitType -> {}
      }
    }

    private void visitExplicitType(PTypeReference pTypeReference) {
      if (!isTypeVarName(pTypeReference.nameText())) {
        scope
            .types()
            .find(pTypeReference.fqn())
            .ifErr(e -> log.log(compileError(pTypeReference.location(), e)));
      }
    }

    private void visitFuncType(PFuncType func) {
      visitType(func.result());
      func.params().forEach(this::visitType);
    }
  }
}
