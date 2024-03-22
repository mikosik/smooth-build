package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import org.smoothbuild.common.base.DecodeHexException;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;

public class DecodeLiterals implements TryFunction1<ModuleP, ModuleP> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "decodeLiterals");
  }

  @Override
  public Try<ModuleP> apply(ModuleP moduleP) {
    var logger = new Logger();
    new DecodeLiteralModuleVisitor(logger).visitModule(moduleP);
    return Try.of(moduleP, logger);
  }

  private static class DecodeLiteralModuleVisitor extends ModuleVisitorP {
    private final Logger logger;

    public DecodeLiteralModuleVisitor(Logger logger) {
      this.logger = logger;
    }

    @Override
    public void visitBlob(BlobP blobP) {
      super.visitBlob(blobP);
      try {
        blobP.decodeByteString();
      } catch (DecodeHexException e) {
        logger.log(compileError(blobP, "Illegal Blob literal: " + e.getMessage()));
      }
    }

    @Override
    public void visitInt(IntP intP) {
      super.visitInt(intP);
      try {
        intP.decodeBigInteger();
      } catch (NumberFormatException e) {
        logger.log(compileError(intP, "Illegal Int literal: `" + intP.literal() + "`."));
      }
    }

    @Override
    public void visitString(StringP stringP) {
      super.visitString(stringP);
      try {
        stringP.calculateUnescaped();
      } catch (UnescapeFailedException e) {
        logger.log(compileError(stringP, "Illegal String literal: " + e.getMessage()));
      }
    }
  }
}
