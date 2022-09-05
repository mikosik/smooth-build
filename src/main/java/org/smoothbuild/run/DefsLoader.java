package org.smoothbuild.run;

import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalMod;
import static org.smoothbuild.compile.ps.LoadMod.loadModule;
import static org.smoothbuild.install.InstallationPaths.SLIB_MODS;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_PATH;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.define.ModS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.install.ModFilesDetector;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class DefsLoader {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SLIB_MODS)
          .add(PRJ_MOD_FILE_PATH)
          .build();

  private final FileResolver fileResolver;
  private final ModFilesDetector modFilesDetector;
  private final Reporter reporter;

  @Inject
  public DefsLoader(FileResolver fileResolver, ModFilesDetector modFilesDetector,
      Reporter reporter) {
    this.fileResolver = fileResolver;
    this.modFilesDetector = modFilesDetector;
    this.reporter = reporter;
  }

  public Optional<DefsS> loadDefs() {
    reporter.startNewPhase("Parsing");

    var internalMod = loadInternalMod();
    var allDefs = DefsS.empty().withModule(internalMod);
    var files = modFilesDetector.detect(MODULES);
    for (Entry<ModPath, ModFiles> entry : files.entrySet()) {
      ModFiles modFiles = entry.getValue();
      Maybe<ModS> module = load(allDefs, entry.getKey(), modFiles);
      reporter.report(modFiles.smoothFile().toString(), module.logs().toList());
      if (module.containsProblem()) {
        return Optional.empty();
      } else {
        allDefs = allDefs.withModule(module.value());
      }
    }
    return Optional.of(allDefs);
  }

  private Maybe<ModS> load(DefsS imported, ModPath path, ModFiles modFiles) {
    var sourceCode = readFileContent(modFiles.smoothFile());
    if (sourceCode.containsProblem()) {
      return maybeLogs(sourceCode.logs());
    } else {
      return loadModule(path, modFiles, sourceCode.value(), imported);
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
