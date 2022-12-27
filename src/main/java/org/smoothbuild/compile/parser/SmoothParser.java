package org.smoothbuild.compile.parser;

import static org.smoothbuild.compile.parser.AntlrParser.antlrParse;
import static org.smoothbuild.compile.parser.ApTranslator.translateAp;
import static org.smoothbuild.compile.parser.FindSyntaxErrors.findSyntaxErrors;

import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

public class SmoothParser {
  public static Maybe<ModuleP> parse(ModFiles modFiles, String sourceCode) {
    return new Parser(modFiles, sourceCode)
        .process();
  }

  private static class Parser extends MaybeProcessor<ModuleP> {
    private final ModFiles modFiles;
    private final String sourceCode;

    private Parser(ModFiles modFiles, String sourceCode) {
      this.modFiles = modFiles;
      this.sourceCode = sourceCode;
    }

    @Override
    protected ModuleP processImpl() throws FailedException {
      var modContext = addLogsAndGetValue(antlrParse(modFiles.smoothFile(), sourceCode));
      var moduleP = addLogsAndGetValue(translateAp(modFiles.smoothFile(), modContext));
      addLogs(findSyntaxErrors(moduleP));
      return moduleP;
    }
  }
}
