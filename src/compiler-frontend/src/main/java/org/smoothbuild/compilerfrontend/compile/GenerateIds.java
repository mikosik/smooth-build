package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.parseReference;
import static org.smoothbuild.compilerfrontend.lang.base.Name.parseReferenceableName;
import static org.smoothbuild.compilerfrontend.lang.base.Name.parseStructName;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.base.Id;

public class GenerateIds implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    new CreateIdVisitor(null, logger).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":verifyIds");
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
    public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws RuntimeException {
      var nameText = pNamedEvaluable.nameText();
      parseReferenceableName(nameText)
          .ifLeft(e -> logIllegalIdentifier(nameText, pNamedEvaluable.location(), e))
          .mapRight(this::fullId)
          .ifRight(id -> {
            pNamedEvaluable.setId(id);
            runWithScopeId(id, () -> super.visitNamedEvaluable(pNamedEvaluable));
          });
    }

    @Override
    public void visitItem(PItem pItem) throws RuntimeException {
      var nameText = pItem.nameText();
      parseReferenceableName(nameText)
          .ifLeft(e -> logIllegalIdentifier(nameText, pItem.location(), e))
          .ifRight(id -> {
            pItem.setId(id);
            var fullId = fullId(id);
            pItem.setDefaultValueId(pItem.defaultValue().map(ignore -> fullId));
          });
      pItem.defaultValue().ifPresent(this::visitExpr);
    }

    @Override
    public void visitLambda(PLambda pLambda) throws RuntimeException {
      var nameText = pLambda.nameText();
      parseReferenceableName(nameText)
          .ifLeft(e -> logIllegalIdentifier(nameText, pLambda.location(), e))
          .mapRight(this::fullId)
          .ifRight(id -> {
            pLambda.setId(id);
            runWithScopeId(id, () -> super.visitLambda(pLambda));
          });
    }

    private void logIllegalIdentifier(String nameText, Location location, String e) {
      logger.log(compileError(location, "`" + nameText + "` is illegal identifier name. " + e));
    }

    @Override
    public void visitStruct(PStruct pStruct) {
      var nameText = pStruct.nameText();
      parseStructName(nameText)
          .ifLeft(e -> logIllegalStructName(pStruct, e, pStruct.nameText()))
          .ifRight(id -> {
            pStruct.setId(id);
            runWithScopeId(fullId(id), () -> super.visitStruct(pStruct));
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
          .ifRight(pReference::setId)
          .ifLeft(e -> logIllegalReference(pReference, e, pReference.nameText()));
    }

    private void logIllegalReference(PReference pReference, String e, String nameText) {
      logger.log(compileError(pReference.location(), "Illegal reference `" + nameText + "`. " + e));
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

    private Id fullId(Id id) {
      return scopeId == null ? id : scopeId.append(id);
    }
  }
}
