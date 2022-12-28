package org.smoothbuild.run;

import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.install.InstallationPaths.SLIB_MODS;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_PATH;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.FsTranslator;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.install.ModFilesDetector;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class DefinitionsLoader {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SLIB_MODS)
          .add(PRJ_MOD_FILE_PATH)
          .build();

  private final FsTranslator fsTranslator;
  private final ModFilesDetector modFilesDetector;
  private final Reporter reporter;

  @Inject
  public DefinitionsLoader(
      FsTranslator fsTranslator, ModFilesDetector modFilesDetector, Reporter reporter) {
    this.fsTranslator = fsTranslator;
    this.modFilesDetector = modFilesDetector;
    this.reporter = reporter;
  }

  public Optional<DefinitionsS> loadDefinitions() {
    reporter.startNewPhase("Parsing");

    var internalMod = loadInternalModule();
    var allDefinitions = DefinitionsS.empty().withModule(internalMod);
    var files = modFilesDetector.detect(MODULES);
    for (ModFiles modFiles : files) {
      var module = fsTranslator.translateFs(modFiles, allDefinitions);
      reporter.report(modFiles.smoothFile().toString(), module.logs().toList());
      if (module.containsProblem()) {
        return Optional.empty();
      } else {
        allDefinitions = allDefinitions.withModule(module.value());
      }
    }
    return Optional.of(allDefinitions);
  }
}
