package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Log.fatal;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.compile.lang.define.ValS;
import org.smoothbuild.compile.sb.SbTranslatorFacade;
import org.smoothbuild.compile.sb.TranslateSbExc;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.Vm;

import com.google.common.collect.ImmutableList;

public class Evaluator {
  private final SbTranslatorFacade sbTranslatorFacade;
  private final Vm vm;
  private final Reporter reporter;

  @Inject
  public Evaluator(SbTranslatorFacade sbTranslatorFacade, Vm vm, Reporter reporter) {
    this.sbTranslatorFacade = sbTranslatorFacade;
    this.vm = vm;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<InstB>> evaluate(List<ValS> vals) {
    try {
      reporter.startNewPhase("Compiling");
      var sbTranslation = sbTranslatorFacade.translate(vals);
      reporter.startNewPhase("Evaluating");
      return vm.evaluate(sbTranslation.exprBs(), sbTranslation.tagLocs());
    } catch (TranslateSbExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Optional.empty();
    }
  }
}
