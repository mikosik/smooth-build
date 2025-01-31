package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.parseReference;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseReferenceableName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseStructName;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeReference;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class GenerateIds implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    new CreateIdVisitor(null, logger).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":generateIds");
    return output(pModule, label, logger.toList());
  }

  private static class CreateIdVisitor extends PModuleVisitor<RuntimeException> {
    private final Logger logger;
    private Id scopeId;

    public CreateIdVisitor(Id scopeId, Logger logger) {
      this.scopeId = scopeId;
      this.logger = logger;
    }

    @Override
    public void visitType(PType pType) throws RuntimeException {
      switch (pType) {
        case PArrayType pArrayType -> visitType(pArrayType.elemT());
        case PFuncType pFuncType -> {
          visitType(pFuncType.result());
          pFuncType.params().forEach(this::visitType);
        }
        case PTypeReference pTypeReference -> parseReference(pTypeReference.nameText())
            .ifErr(e -> logIllegalTypeReference(pTypeReference, e))
            .ifOk(pTypeReference::setId);
        case PImplicitType pImplicitType -> {}
      }
    }

    @Override
    public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws RuntimeException {
      var nameText = pNamedEvaluable.nameText();
      parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pNamedEvaluable.location(), e))
          .mapOk(this::toFqn)
          .ifOk(pNamedEvaluable::setFqn)
          .ifOk(id -> runWithScopeId(id, () -> super.visitNamedEvaluable(pNamedEvaluable)));
    }

    @Override
    public void visitLambda(PLambda pLambda) throws RuntimeException {
      var nameText = pLambda.nameText();
      parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pLambda.location(), e))
          .mapOk(this::toFqn)
          .ifOk(pLambda::setFqn)
          .ifOk(id -> runWithScopeId(id, () -> super.visitLambda(pLambda)));
    }

    private void logIllegalTypeReference(PTypeReference pTypeReference, String e) {
      var message = "Illegal type reference `" + pTypeReference.nameText() + "`. " + e;
      logger.log(compileError(pTypeReference.location(), message));
    }

    @Override
    public void visitItem(PItem pItem) throws RuntimeException {
      var nameText = pItem.nameText();
      parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pItem.location(), e))
          .ifOk(name -> {
            var fqn = toFqn(name);
            pItem.setFqn(fqn);
            pItem.setDefaultValueId(pItem.defaultValue().map(ignore -> fqn));
          });
      pItem.defaultValue().ifPresent(this::visitExpr);
      visitType(pItem.type());
    }

    private void logIllegalIdentifier(String nameText, Location location, String e) {
      logger.log(compileError(location, "`" + nameText + "` is illegal identifier name. " + e));
    }

    @Override
    public void visitStruct(PStruct pStruct) {
      var nameText = pStruct.nameText();
      parseStructName(nameText)
          .ifErr(e -> logIllegalStructName(pStruct, e, pStruct.nameText()))
          .ifOk(name -> {
            var fqn = toFqn(name);
            pStruct.setFqn(fqn);
            runWithScopeId(fqn, () -> super.visitStruct(pStruct));
          });
    }

    private void logIllegalStructName(PStruct pStruct, String e, String nameText) {
      logger.log(
          compileError(pStruct.location(), "`" + nameText + "` is illegal struct name. " + e));
    }

    @Override
    public void visitReference(PReference pReference) throws RuntimeException {
      super.visitReference(pReference);
      parseReference(pReference.nameText())
          .ifOk(pReference::setId)
          .ifErr(e -> logIllegalReference(pReference, e, pReference.nameText()));
    }

    private void logIllegalReference(PReference pReference, String e, String nameText) {
      logger.log(compileError(pReference.location(), "Illegal reference `" + nameText + "`. " + e));
    }

    @Override
    public void visitNamedArg(PNamedArg pNamedArg) throws RuntimeException {
      super.visitNamedArg(pNamedArg);
      parseReferenceableName(pNamedArg.nameText())
          .ifOk(pNamedArg::setName)
          .ifErr(e -> logIllegalParamName(pNamedArg, e));
    }

    private void logIllegalParamName(PNamedArg pNamedArg, String e) {
      logger.log(compileError(
          pNamedArg.location(), "`" + pNamedArg.nameText() + "` is illegal parameter name. " + e));
    }

    @Override
    public void visitSelect(PSelect pSelect) throws RuntimeException {
      super.visitSelect(pSelect);
      parseReferenceableName(pSelect.fieldNameText())
          .ifOk(pSelect::setFieldName)
          .ifErr(e -> logIllegalFieldName(pSelect, e));
    }

    private void logIllegalFieldName(PSelect pSelect, String e) {
      var message = "`" + pSelect.fieldNameText() + "` is illegal field name. " + e;
      logger.log(compileError(pSelect.location(), message));
    }

    private void runWithScopeId(Id id, Runnable runnable) {
      var old = scopeId;
      scopeId = id;
      try {
        runnable.run();
      } finally {
        scopeId = old;
      }
    }

    private Fqn toFqn(Name name) {
      return scopeId == null ? Fqn.fqn(name.toString()) : scopeId.append(name);
    }
  }
}
