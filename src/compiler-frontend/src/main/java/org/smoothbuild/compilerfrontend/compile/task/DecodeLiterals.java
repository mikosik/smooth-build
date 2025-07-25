package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import org.smoothbuild.common.base.DecodeHexException;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPosition;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;

public class DecodeLiterals implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var decodeLiteralModuleVisitor = new DecodeLiteralModuleVisitor();
    decodeLiteralModuleVisitor.visit(pModule);
    var label = COMPILER_FRONT_LABEL.append(":decodeLiterals");
    return output(pModule, label, decodeLiteralModuleVisitor.logger.toList());
  }

  private static class DecodeLiteralModuleVisitor extends PScopingModuleVisitor<RuntimeException> {
    private final Logger logger = new Logger();

    @Override
    public void visitBlob(PBlob pBlob) {
      super.visitBlob(pBlob);
      try {
        pBlob.decodeByteString();
      } catch (DecodeHexException e) {
        logger.log(compileError(pBlob, "Illegal Blob literal: " + e.getMessage()));
      }
    }

    @Override
    public void visitInt(PInt pInt) {
      super.visitInt(pInt);
      try {
        pInt.decodeBigInteger();
      } catch (NumberFormatException e) {
        logger.log(compileError(pInt, "Illegal Int literal: `" + pInt.literal() + "`."));
      }
    }

    @Override
    public void visitPosition(PPosition pPosition) {
      super.visitPosition(pPosition);
      try {
        pPosition.decodeBigInteger();
      } catch (NumberFormatException e) {
        logger.log(
            compileError(pPosition, "Illegal tuple position: `" + pPosition.literal() + "`."));
      }
    }

    @Override
    public void visitString(PString pString) {
      super.visitString(pString);
      try {
        pString.calculateUnescaped();
      } catch (UnescapeFailedException e) {
        logger.log(compileError(pString, "Illegal String literal: " + e.getMessage()));
      }
    }
  }
}
