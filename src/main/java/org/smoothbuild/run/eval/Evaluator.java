package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.sb.SbTranslator;
import org.smoothbuild.compile.sb.SbTranslatorProv;
import org.smoothbuild.compile.sb.TranslateSbExc;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.Vm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Evaluator {
  private final SbTranslatorProv sbTranslatorProv;
  private final Vm vm;
  private final Reporter reporter;

  @Inject
  public Evaluator(SbTranslatorProv sbTranslatorProv, Vm vm, Reporter reporter) {
    this.sbTranslatorProv = sbTranslatorProv;
    this.vm = vm;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<ValB>> evaluate(List<? extends ExprS> exprsS) {
    reporter.startNewPhase("Compiling");
    var sbTranslator = sbTranslatorProv.get();
    var exprsB = translate(exprsS, sbTranslator);
    if (exprsB.isEmpty()) {
      return Optional.empty();
    }

    reporter.startNewPhase("Evaluating");
    return evaluate(vm, exprsB.get(), sbTranslator.descriptions());
  }

  private Optional<ImmutableList<ValB>> evaluate(Vm vm, ImmutableList<ExprB> exprs,
      ImmutableMap<ExprB, LabeledLoc> descriptions) {
    try {
      return vm.evaluate(exprs, descriptions);
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Optional.empty();
    }
  }

  private Optional<ImmutableList<ExprB>> translate(
      List<? extends ExprS> exprsS, SbTranslator sbTranslator) {
    try {
      return Optional.of(map(exprsS, sbTranslator::translateExpr));
    } catch (TranslateSbExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }
}
