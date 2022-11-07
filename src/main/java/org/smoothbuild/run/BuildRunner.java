package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.FindTopValues.findTopValues;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.zip;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.compile.lang.define.PolyValS;
import org.smoothbuild.compile.lang.define.ValS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.ArtifactSaver;
import org.smoothbuild.run.eval.Evaluator;
import org.smoothbuild.run.eval.EvaluatorExc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BuildRunner {
  private final ArtifactsRemover artifactsRemover;
  private final DefsLoader defsLoader;
  private final Evaluator evaluator;
  private final ArtifactSaver artifactSaver;
  private final Reporter reporter;

  @Inject
  public BuildRunner(ArtifactsRemover artifactsRemover, DefsLoader defsLoader,
      Evaluator evaluator, ArtifactSaver artifactSaver, Reporter reporter) {
    this.artifactsRemover = artifactsRemover;
    this.defsLoader = defsLoader;
    this.evaluator = evaluator;
    this.artifactSaver = artifactSaver;
    this.reporter = reporter;
  }

  public int run(List<String> names) {
    var artifactsOpt = evaluate(names);
    if (artifactsOpt.isEmpty()) {
      reporter.printSummary();
      return EXIT_CODE_ERROR;
    }

    int exitCode = artifactSaver.saveArtifacts(artifactsOpt.get());
    reporter.printSummary();
    return exitCode;
  }

  public Optional<ImmutableMap<ValS, InstB>> evaluate(List<String> names) {
    if (artifactsRemover.removeArtifacts() == EXIT_CODE_ERROR) {
      return Optional.empty();
    }

    var defsOpt = defsLoader.loadDefs();
    if (defsOpt.isEmpty()) {
      return Optional.empty();
    }

    var defs = defsOpt.get();
    var evaluablesOpt = findTopValues(reporter, defs, names);
    if (evaluablesOpt.isEmpty()) {
      return Optional.empty();
    }

    var evaluables = evaluablesOpt.get();
    var evaluationsOpt = evaluate(evaluables);
    if (evaluationsOpt.isEmpty()) {
      return Optional.empty();
    }
    var values = evaluationsOpt.get();
    var monoEvaluables = map(evaluables, PolyValS::mono);
    return Optional.of(zip(monoEvaluables, values));
  }

  private Optional<ImmutableList<InstB>> evaluate(ImmutableList<PolyValS> evaluables) {
    try {
      return evaluator.evaluate(evaluables);
    } catch (EvaluatorExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }
}
