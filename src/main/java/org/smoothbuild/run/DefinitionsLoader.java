package org.smoothbuild.run;

import static org.smoothbuild.install.InstallationPaths.SLIB_MODS;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_PATH;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.install.ModuleResourcesDetector;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class DefinitionsLoader {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SLIB_MODS)
          .add(PRJ_MOD_FILE_PATH)
          .build();

  private final FsTranslator fsTranslator;
  private final ModuleResourcesDetector moduleResourcesDetector;
  private final Reporter reporter;

  @Inject
  public DefinitionsLoader(
      FsTranslator fsTranslator,
      ModuleResourcesDetector moduleResourcesDetector,
      Reporter reporter) {
    this.fsTranslator = fsTranslator;
    this.moduleResourcesDetector = moduleResourcesDetector;
    this.reporter = reporter;
  }

  public Optional<ScopeS> loadDefinitions() {
    reporter.startNewPhase("Parsing");
    var modules = moduleResourcesDetector.detect(MODULES);
    var definitions = fsTranslator.translateFs(modules);
    definitions.logs().forEach(reporter::report);
    return definitions.valueOptional();
  }
}
