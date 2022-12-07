package org.smoothbuild.run.eval;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.sb.SbTranslatorExc;
import org.smoothbuild.compile.sb.SbTranslatorFacade;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

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

  public Optional<ImmutableList<ValueB>> evaluate(ImmutableList<? extends ExprS> exprs)
      throws EvaluatorExcS {
    try {
      reporter.startNewPhase("Compiling");
      var sbTranslation = sbTranslatorFacade.translate(exprs);
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