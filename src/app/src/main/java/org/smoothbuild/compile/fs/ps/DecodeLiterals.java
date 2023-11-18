package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.CompileError.compileError;

import java.util.function.Function;

import org.smoothbuild.common.DecodeHexExc;
import org.smoothbuild.common.UnescapingFailedExc;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Maybe;

public class DecodeLiterals implements Function<ModuleP, Maybe<ModuleP>> {
  @Override
  public Maybe<ModuleP> apply(ModuleP moduleP) {
    var logBuffer = new LogBuffer();
    new DecodeLiteralModuleVisitor(logBuffer).visitModule(moduleP);
    return Maybe.of(moduleP, logBuffer);
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
      } catch (DecodeHexExc e) {
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
      } catch (UnescapingFailedExc e) {
        logger.log(compileError(stringP, "Illegal String literal: " + e.getMessage()));
      }
    }
  }
}
