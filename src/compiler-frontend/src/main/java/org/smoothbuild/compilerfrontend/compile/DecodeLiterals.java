package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import org.smoothbuild.common.base.DecodeHexException;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;

public class DecodeLiterals implements TryFunction1<PModule, PModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "decodeLiterals");
  }

  @Override
  public Try<PModule> apply(PModule pModule) {
    var logger = new Logger();
    new DecodeLiteralModuleVisitor(logger).visitModule(pModule);
    return Try.of(pModule, logger);
  }

  private static class DecodeLiteralModuleVisitor extends PModuleVisitor {
    private final Logger logger;

    public DecodeLiteralModuleVisitor(Logger logger) {
      this.logger = logger;
    }

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
