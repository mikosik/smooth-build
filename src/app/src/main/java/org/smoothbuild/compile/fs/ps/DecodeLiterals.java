package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.CompileError.compileError;

import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
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
    }.visitModule(moduleP);
  }
}
