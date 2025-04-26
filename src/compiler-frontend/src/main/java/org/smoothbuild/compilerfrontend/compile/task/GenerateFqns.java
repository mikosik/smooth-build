package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.parseReference;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseReferenceableName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseTypeName;

import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolyEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStructSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTupleType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeParam;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeReference;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class GenerateFqns implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    new Visitor(logger).visit(pModule);
    var label = COMPILER_FRONT_LABEL.append(":generateIds");
    return output(pModule, label, logger.toList());
  }

  private static class Visitor extends PModuleVisitor<RuntimeException> {
    private final Logger logger;
    private Fqn fqn;

    public Visitor(Logger logger) {
      this.logger = logger;
      this.fqn = null;
    }

    @Override
    public void visit(PContainer pContainer) {
      var name =
          switch (pContainer) {
            case PLambda pLambda -> nameOf(pLambda);
            case PModule pModule -> Result.<Name>err("");
            case PPolyEvaluable pPolyEvaluable -> Result.<Name>err("");
            case PNamedEvaluable pNamedEvaluable -> nameOf(pNamedEvaluable);
            case PStruct pStruct -> nameOf(pStruct);
          };

      var oldFqn = fqn;
      try {
        fqn = name.mapOk(this::toFqn).ifOk(pContainer::setFqn).okOr(null);
        super.visit(pContainer);
      } finally {
        fqn = oldFqn;
      }
    }

    @Override
    public void visitType(PType pType) throws RuntimeException {
      switch (pType) {
        case PArrayType pArrayType -> visitType(pArrayType.elemT());
        case PFuncType pFuncType -> {
          visitType(pFuncType.result());
          pFuncType.params().forEach(this::visitType);
        }
        case PTupleType pTupleType -> pTupleType.elementTypes().forEach(this::visitType);
        case PTypeReference pTypeReference ->
          parseReference(pTypeReference.nameText())
              .ifErr(e -> logIllegalTypeReference(pTypeReference, e))
              .ifOk(pTypeReference::setFqn);
        case PImplicitType pImplicitType -> {}
      }
    }

    private Result<Name> nameOf(PNamedEvaluable pNamedEvaluable) throws RuntimeException {
      var nameText = pNamedEvaluable.nameText();
      return parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pNamedEvaluable.location(), e));
    }

    private Result<Name> nameOf(PLambda pLambda) throws RuntimeException {
      var nameText = pLambda.nameText();
      return parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pLambda.location(), e));
    }

    private void logIllegalTypeReference(PTypeReference pTypeReference, String e) {
      var message = "Illegal type reference `" + pTypeReference.nameText() + "`. " + e;
      logger.log(compileError(pTypeReference.location(), message));
    }

    @Override
    public void visitTypeParam(PTypeParam pTypeParam) {
      parseTypeName(pTypeParam.nameText())
          .ifErr(e -> logIllegalTypeVar(pTypeParam.nameText(), pTypeParam.location(), e))
          .ifOk(pTypeParam::setName);
    }

    private void logIllegalTypeVar(String nameText, Location location, String e) {
      logger.log(compileError(location, "`" + nameText + "` is illegal type variable name. " + e));
    }

    @Override
    public void visitItem(PItem pItem) throws RuntimeException {
      var nameText = pItem.nameText();
      parseReferenceableName(nameText)
          .ifErr(e -> logIllegalIdentifier(nameText, pItem.location(), e))
          .ifOk(name -> {
            var fqn = toFqn(name);
            pItem.setFqn(fqn);
            pItem
                .defaultValue()
                .ifPresent(defaultValue -> defaultValue.setFqn(
                    fqn(pItem.fqn().parent() + "~" + pItem.name().toString())));
          });
      pItem.defaultValue().ifPresent(pDefaultValue -> visitExpr(pDefaultValue.expr()));
      visitType(pItem.pType());
    }

    private void logIllegalIdentifier(String nameText, Location location, String e) {
      logger.log(compileError(location, "`" + nameText + "` is illegal identifier name. " + e));
    }

    private Result<Name> nameOf(PStruct pStruct) {
      var nameText = pStruct.nameText();
      return parseTypeName(nameText)
          .ifErr(e -> logIllegalStructName(pStruct, e, pStruct.nameText()));
    }

    private void logIllegalStructName(PStruct pStruct, String e, String nameText) {
      logger.log(
          compileError(pStruct.location(), "`" + nameText + "` is illegal struct name. " + e));
    }

    @Override
    public void visitReference(PReference pReference) throws RuntimeException {
      super.visitReference(pReference);
      parseReference(pReference.nameText())
          .ifOk(pReference::setFqn)
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
    public void visitStructSelect(PStructSelect pStructSelect) throws RuntimeException {
      super.visitStructSelect(pStructSelect);
      parseReferenceableName(pStructSelect.fieldNameText())
          .ifOk(pStructSelect::setFieldName)
          .ifErr(e -> logIllegalFieldName(pStructSelect, e));
    }

    private void logIllegalFieldName(PStructSelect pStructSelect, String e) {
      var message = "`" + pStructSelect.fieldNameText() + "` is illegal field name. " + e;
      logger.log(compileError(pStructSelect.location(), message));
    }

    private Fqn toFqn(Name name) {
      return fqn == null ? fqn(name.toString()) : fqn.append(name);
    }
  }
}
