package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;

import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.UnescapingFailedExc;

public class DecodeLiterals {
  public static ImmutableLogs decodeLiterals(ModuleP moduleP) {
    var logBuffer = new LogBuffer();
    decodeLiterals(moduleP, logBuffer);
    return logBuffer.toImmutableLogs();
  }

  private static void decodeLiterals(ModuleP moduleP, Logger logger) {
    new ModuleVisitorP() {
      @Override
      public void visitBlob(BlobP blob) {
        super.visitBlob(blob);
        try {
          blob.decodeByteString();
        } catch (DecodeHexExc e) {
          logger.log(compileError(blob, "Illegal Blob literal: " + e.getMessage()));
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
      public void visitString(StringP string) {
        super.visitString(string);
        try {
          string.calculateUnescaped();
        } catch (UnescapingFailedExc e) {
          logger.log(compileError(string, "Illegal String literal: " + e.getMessage()));
        }
      }
    }.visitModule(moduleP);
  }
}
