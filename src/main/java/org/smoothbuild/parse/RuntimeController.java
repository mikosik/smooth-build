package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;
import static org.smoothbuild.util.Lists.concat;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.install.ProjectPaths;
import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.runtime.SRuntime;

import com.google.common.collect.ImmutableMap;

public class RuntimeController {
  private final SRuntime runtime;
  private final InstallationPaths installationPaths;
  private final ProjectPaths projectPaths;
  private final Reporter reporter;

  @Inject
  public RuntimeController(SRuntime runtime, InstallationPaths installationPaths,
      ProjectPaths projectPaths, Reporter reporter) {
    this.runtime = runtime;
    this.installationPaths = installationPaths;
    this.projectPaths = projectPaths;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    reporter.startNewPhase("Parsing");

    ObjectFactory objectFactory = runtime.objectFactory();
    ImmutableMap<String, Type> basicTypes = Types.BASIC_TYPES.stream()
        .map(t -> objectFactory.getType(t.name()))
        .collect(toImmutableMap(Type::name, t -> t));
    Definitions definitions = new Definitions(basicTypes, ImmutableMap.of());
    for (ModulePath mPath : concat(installationPaths.slibModules(), projectPaths.userModule())) {
      try (LoggerImpl logger = new LoggerImpl(mPath.smooth().shorted(), reporter)) {
        Definitions module = loadModule(runtime, definitions, mPath, logger);
        definitions = Definitions.union(definitions, module);
      }
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      }
    }
    runner.accept(runtime);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
