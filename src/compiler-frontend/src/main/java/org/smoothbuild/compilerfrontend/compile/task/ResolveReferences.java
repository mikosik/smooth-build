package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTupleType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeReference;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;

/**
 * For each PReference or PTypeReference it resolves its .fqn() and stores result in
 * PReference.setReferenced() or PTypeReference.setReferenced().
 */
public class ResolveReferences implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var detector = new Detector();
    detector.visit(pModule);
    var label = COMPILER_FRONT_LABEL.append(":detectUndefined");
    return output(pModule, label, detector.logger.toList());
  }

  private static class Detector extends PScopingModuleVisitor<RuntimeException> {
    private final Logger logger = new Logger();

    @Override
    public void visitNamedFunc(PNamedFunc pNamedFunc) throws RuntimeException {
      // Do not check generated constructor as any problem it can have is caused by problem
      // in its struct which is reported separately.
      if (!(pNamedFunc instanceof PConstructor)) {
        super.visitNamedFunc(pNamedFunc);
      }
    }

    @Override
    public void visitReference(PReference pReference) {
      var fqn = pReference.fqn();
      scope()
          .referenceables()
          .find(fqn)
          .ifOk(pReference::setReferenced)
          .ifErr(e -> logger.log(compileError(pReference, e)));
    }

    @Override
    public void visitItem(PItem pItem) throws RuntimeException {
      super.visitItem(pItem);
      pItem.defaultValue().ifPresent(dv -> scope()
          .referenceables()
          .find(dv.fqn())
          .ifOk(referenced -> dv.setReferenced((PolyEvaluable) referenced))
          .ifErr(e -> logger.log(compileError(dv, e))));
    }

    @Override
    public void visitType(PType pType) {
      switch (pType) {
        case PArrayType array -> visitType(array.elementType());
        case PFuncType func -> visitFuncType(func);
        case PTupleType tuple -> visitTupleType(tuple);
        case PTypeReference pTypeReference -> visitExplicitType(pTypeReference);
        case PImplicitType _ -> {}
      }
    }

    private void visitExplicitType(PTypeReference pTypeReference) {
      scope()
          .types()
          .find(pTypeReference.fqn())
          .ifOk(pTypeReference::setReferenced)
          .ifErr(e -> logger.log(compileError(pTypeReference.location(), e)));
    }

    private void visitFuncType(PFuncType func) {
      visitType(func.result());
      func.params().forEach(this::visitType);
    }

    private void visitTupleType(PTupleType tuple) {
      tuple.elementTypes().forEach(this::visitType);
    }
  }
}
