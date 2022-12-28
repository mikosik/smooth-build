package org.smoothbuild.run;

import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.parser.SmoothParser.parse;
import static org.smoothbuild.compile.ps.PsTranslator.translatePs;
import static org.smoothbuild.install.InstallationPaths.SLIB_MODS;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_PATH;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.install.ModFilesDetector;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class DefinitionsLoader {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SLIB_MODS)
          .add(PRJ_MOD_FILE_PATH)
          .build();

  private final FileResolver fileResolver;
  private final ModFilesDetector modFilesDetector;
  private final Reporter reporter;

  @Inject
  public DefinitionsLoader(
      FileResolver fileResolver, ModFilesDetector modFilesDetector, Reporter reporter) {
    this.fileResolver = fileResolver;
    this.modFilesDetector = modFilesDetector;
    this.reporter = reporter;
  }

  public Optional<DefinitionsS> loadDefinitions() {
    reporter.startNewPhase("Parsing");

    var internalMod = loadInternalModule();
    var allDefinitions = DefinitionsS.empty().withModule(internalMod);
    var files = modFilesDetector.detect(MODULES);
    for (ModFiles modFiles : files) {
      Maybe<ModuleS> module = load(allDefinitions, modFiles);
      reporter.report(modFiles.smoothFile().toString(), module.logs().toList());
      if (module.containsProblem()) {
        return Optional.empty();
      } else {
        allDefinitions = allDefinitions.withModule(module.value());
      }
    }
    return Optional.of(allDefinitions);
  }

  private Maybe<ModuleS> load(DefinitionsS imported, ModFiles modFiles) {
    var sourceCode = readFileContent(modFiles.smoothFile());
    if (sourceCode.containsProblem()) {
      return maybeLogs(sourceCode.logs());
    } else {
      Maybe<ModuleP> parse = parse(modFiles, sourceCode.value());
      if (parse.containsProblem()) {
        return maybeLogs(parse.logs());
      } else {
        return translatePs(parse.value(), imported);
      }
    }
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
