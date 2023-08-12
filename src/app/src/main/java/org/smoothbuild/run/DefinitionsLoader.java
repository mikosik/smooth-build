package org.smoothbuild.run;

import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_MODS;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_FILE_PATH;

import java.util.Optional;

import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.filesystem.install.ModuleResourcesDetector;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;

public class DefinitionsLoader {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(STD_LIB_MODS)
          .add(DEFAULT_MODULE_FILE_PATH)
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
