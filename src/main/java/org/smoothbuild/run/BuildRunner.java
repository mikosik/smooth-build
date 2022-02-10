package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.run.FindTopEvals.findTopEvaluables;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.ArtifactSaver;
import org.smoothbuild.run.eval.Evaluator;
import org.smoothbuild.util.collect.Maps;

import com.google.common.collect.ImmutableList;

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

  public Optional<Map<TopRefS, ValB>> evaluate(List<String> names) {
    if (artifactsRemover.removeArtifacts() == EXIT_CODE_ERROR) {
      return Optional.empty();
    }

    var defsOpt = defsLoader.loadDefs();
    if (defsOpt.isEmpty()) {
      return Optional.empty();
    }

    var defs = defsOpt.get();
    var evaluablesOpt = findTopEvaluables(reporter, defs, names);
    if (evaluablesOpt.isEmpty()) {
      return Optional.empty();
    }

    var evaluables = evaluablesOpt.get();
    var evaluationsOpt = evaluator.evaluate(defs, evaluables);
    if (evaluationsOpt.isEmpty()) {
      return Optional.empty();
    }
    ImmutableList<ValB> listB = evaluationsOpt.get();
    return Optional.of(Maps.zip(evaluables, listB));
  }
}
