package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.compile.fs.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.FindTopValues.findTopValues;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.InstantiateS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.compile.fs.lang.define.ReferenceS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.ArtifactSaver;
import org.smoothbuild.run.eval.EvaluatorExcS;
import org.smoothbuild.run.eval.EvaluatorS;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Maps;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import jakarta.inject.Inject;

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
    return definitionsLoader.loadDefinitions()
        .flatMap(d -> evaluate(d.evaluables(), names));
  }

  private Optional<ImmutableMap<NamedValueS, ValueB>> evaluate(
      ImmutableBindings<NamedEvaluableS> evaluables, List<String> names) {
    var valuesOpt = findTopValues(reporter, evaluables, names);
    var evaluationsOpt = valuesOpt.flatMap(namedValues -> evaluate(evaluables, namedValues));
    return mapPair(valuesOpt, evaluationsOpt, Maps::zip);
  }

  private Optional<ImmutableList<ValueB>> evaluate(
      ImmutableBindings<NamedEvaluableS> evaluables, ImmutableList<NamedValueS> namedValues) {
    var exprs = map(namedValues, v -> new InstantiateS(referenceTo(v), commandLineLocation()));
    try {
      return evaluator.evaluate(evaluables, exprs);
    } catch (EvaluatorExcS e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }

  private static ReferenceS referenceTo(NamedValueS namedValueS) {
    return new ReferenceS(namedValueS.schema(), namedValueS.name(), commandLineLocation());
  }
}
