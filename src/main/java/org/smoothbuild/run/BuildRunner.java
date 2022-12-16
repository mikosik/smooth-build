package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.compile.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.FindTopValues.findTopValues;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.lang.define.EvaluableRefS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedValueS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.ArtifactSaver;
import org.smoothbuild.run.eval.EvaluatorExcS;
import org.smoothbuild.run.eval.EvaluatorS;
import org.smoothbuild.util.collect.Maps;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BuildRunner {
  private final ArtifactsRemover artifactsRemover;
  private final DefinitionsLoader definitionsLoader;
  private final EvaluatorS evaluator;
  private final ArtifactSaver artifactSaver;
  private final Reporter reporter;

  @Inject
  public BuildRunner(
      ArtifactsRemover artifactsRemover,
      DefinitionsLoader definitionsLoader,
      EvaluatorS evaluator,
      ArtifactSaver artifactSaver,
      Reporter reporter) {
    this.artifactsRemover = artifactsRemover;
    this.definitionsLoader = definitionsLoader;
    this.evaluator = evaluator;
    this.artifactSaver = artifactSaver;
    this.reporter = reporter;
  }

  public int run(List<String> names) {
    if (artifactsRemover.removeArtifacts() == EXIT_CODE_ERROR) {
      reporter.printSummary();
      return EXIT_CODE_ERROR;
    }

    var artifactsOpt = evaluate(names);
    if (artifactsOpt.isEmpty()) {
      reporter.printSummary();
      return EXIT_CODE_ERROR;
    }

    int exitCode = artifactSaver.saveArtifacts(artifactsOpt.get());
    reporter.printSummary();
    return exitCode;
  }

  public Optional<ImmutableMap<NamedValueS, ValueB>> evaluate(List<String> names) {
    var defsOpt = definitionsLoader.loadDefinitions();
    var evaluablesOpt = defsOpt.flatMap(d -> findTopValues(reporter, d, names));
    var evaluationsOpt = evaluablesOpt.flatMap(this::evaluate);
    return mapPair(evaluablesOpt, evaluationsOpt, Maps::zip);
  }

  private Optional<ImmutableList<ValueB>> evaluate(ImmutableList<NamedValueS> namedValues) {
    var exprs = map(namedValues,
        v -> new MonoizeS(new EvaluableRefS(v, commandLineLocation()), commandLineLocation()));
    try {
      return evaluator.evaluate(exprs);
    } catch (EvaluatorExcS e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }
}
