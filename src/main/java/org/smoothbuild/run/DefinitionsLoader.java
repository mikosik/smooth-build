package org.smoothbuild.run;

import static org.smoothbuild.install.InstallationPaths.SLIB_MODS;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_PATH;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
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
    var files = modFilesDetector.detect(MODULES);
    var definitions = fsTranslator.translateFs(files);
    if (definitions.containsProblem()) {
      definitions.logs().forEach(reporter::report);
      return Optional.empty();
    } else {
      return Optional.of(definitions.value());
    }
  }
}
