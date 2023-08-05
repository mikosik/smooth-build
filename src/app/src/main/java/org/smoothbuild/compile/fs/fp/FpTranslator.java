package org.smoothbuild.compile.fs.fp;

import static org.smoothbuild.compile.fs.fp.AntlrParser.antlrParse;
import static org.smoothbuild.compile.fs.fp.ApTranslator.translateAp;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

import jakarta.inject.Inject;

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

  public Maybe<ModuleP> translateFp(ModuleResources moduleResources) {
    return new Parser(fileResolver, moduleResources)
        .process();
  }

  private static class Parser extends MaybeProcessor<ModuleP> {
    private final FileResolver fileResolver;
    private final ModuleResources moduleResources;

    private Parser(FileResolver fileResolver, ModuleResources moduleResources) {
      this.fileResolver = fileResolver;
      this.moduleResources = moduleResources;
    }

    @Override
    protected ModuleP processImpl() throws FailedException {
      var sourceCode = addLogsAndGetValue(readFileContent(moduleResources.smoothFile()));
      var modContext = addLogsAndGetValue(antlrParse(moduleResources.smoothFile(), sourceCode));
      var moduleP = addLogsAndGetValue(translateAp(moduleResources.smoothFile(), modContext));
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
