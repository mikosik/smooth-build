package org.smoothbuild.run.eval;

import java.util.Optional;

import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.sb.SbTranslatorExc;
import org.smoothbuild.compile.sb.SbTranslatorFacade;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;

public class EvaluatorS {
  private final SbTranslatorFacade sbTranslatorFacade;
  private final EvaluatorBFactory evaluatorBFactory;
  private final Reporter reporter;

  @Inject
  public EvaluatorS(SbTranslatorFacade sbTranslatorFacade, EvaluatorBFactory evaluatorBFactory,
      Reporter reporter) {
    this.sbTranslatorFacade = sbTranslatorFacade;
    this.evaluatorBFactory = evaluatorBFactory;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<ValueB>> evaluate(
      ImmutableBindings<NamedEvaluableS> evaluables, ImmutableList<? extends ExprS> exprs)
      throws EvaluatorExcS {
    try {
      reporter.startNewPhase("Compiling");
      var sbTranslation = sbTranslatorFacade.translate(evaluables, exprs);
      reporter.startNewPhase("Evaluating");
      var vm = evaluatorBFactory.newEvaluatorB(sbTranslation.bsMapping());
      return vm.evaluate(sbTranslation.exprBs());
    } catch (SbTranslatorExc e) {
      throw new EvaluatorExcS(e.getMessage());
    } catch (InterruptedException e) {
      throw new EvaluatorExcS("Evaluation process has been interrupted.");
    }
  }
}
