package org.smoothbuild.compile.fs.fp;

import static org.smoothbuild.compile.fs.fp.AntlrParser.antlrParse;
import static org.smoothbuild.compile.fs.fp.ApTranslator.translateAp;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.lang.define.ModFiles;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

/**
 * F->P Translator.
 * Translates source code F-ile to P-arsed tree.
 */
public class FpTranslator {
  private final FileResolver fileResolver;

  @Inject
  public FpTranslator(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public Maybe<ModuleP> translateFp(ModFiles modFiles) {
    return new Parser(fileResolver, modFiles)
        .process();
  }

  private static class Parser extends MaybeProcessor<ModuleP> {
    private final FileResolver fileResolver;
    private final ModFiles modFiles;

    private Parser(FileResolver fileResolver, ModFiles modFiles) {
      this.fileResolver = fileResolver;
      this.modFiles = modFiles;
    }

    @Override
    protected ModuleP processImpl() throws FailedException {
      var sourceCode = addLogsAndGetValue(readFileContent(modFiles.smoothFile()));
      var modContext = addLogsAndGetValue(antlrParse(modFiles.smoothFile(), sourceCode));
      var moduleP = addLogsAndGetValue(translateAp(modFiles.smoothFile(), modContext));
      addLogs(FindSyntaxErrors.findSyntaxErrors(moduleP));
      return moduleP;
    }

    private Maybe<String> readFileContent(FilePath filePath) {
      try {
        return maybe(fileResolver.readFileContentAndCacheHash(filePath));
      } catch (NoSuchFileException e) {
        return maybeLogs(error("'" + filePath + "' doesn't exist."));
      } catch (IOException e) {
        return maybeLogs(error("Cannot read build script file '" + filePath + "'."));
      }
    }
  }
}
