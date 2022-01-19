package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.run.FindTopEvals.findTopEvaluables;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.eval.Evaluator;
import org.smoothbuild.eval.artifact.ArtifactSaver;

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
    if (artifactsRemover.removeArtifacts() == EXIT_CODE_ERROR) {
      return printSummaryAndExitWithErrorCode();
    }

    var defsOpt = defsLoader.loadDefs();
    if (defsOpt.isEmpty()) {
      return printSummaryAndExitWithErrorCode();
    }

    var defs = defsOpt.get();
    var evaluablesOpt = findTopEvaluables(reporter, defs, names);
    if (evaluablesOpt.isEmpty()) {
      return printSummaryAndExitWithErrorCode();
    }

    var evaluationsOpt = evaluator.evaluate(defs, evaluablesOpt.get());
    if (evaluationsOpt.isEmpty()) {
      return printSummaryAndExitWithErrorCode();
    }
    if (artifactSaver.saveArtifacts(evaluationsOpt.get()) == EXIT_CODE_ERROR) {
      return printSummaryAndExitWithErrorCode();
    }
    reporter.printSummary();
    return EXIT_CODE_SUCCESS;
  }

  private int printSummaryAndExitWithErrorCode() {
    reporter.printSummary();
    return EXIT_CODE_ERROR;
  }
}
