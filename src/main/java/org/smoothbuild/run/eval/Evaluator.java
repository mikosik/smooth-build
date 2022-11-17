package org.smoothbuild.run.eval;

import static org.smoothbuild.compile.lang.base.Loc.commandLineLoc;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedValueS;
import org.smoothbuild.compile.lang.define.PolyRefS;
import org.smoothbuild.compile.sb.SbTranslatorExc;
import org.smoothbuild.compile.sb.SbTranslatorFacade;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class Evaluator {
  private final SbTranslatorFacade sbTranslatorFacade;
  private final VmFactory vmFactory;
  private final Reporter reporter;

  @Inject
  public Evaluator(SbTranslatorFacade sbTranslatorFacade, VmFactory vmFactory,
      Reporter reporter) {
    this.sbTranslatorFacade = sbTranslatorFacade;
    this.vmFactory = vmFactory;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<ValueB>> evaluate(ImmutableList<NamedValueS> values)
      throws EvaluatorExc {
    try {
      var loc = commandLineLoc();
      var refs = map(values, v -> new MonoizeS(new PolyRefS(v, loc), loc));
      reporter.startNewPhase("Compiling");
      var sbTranslation = sbTranslatorFacade.translate(refs);
      reporter.startNewPhase("Evaluating");
      var vm = vmFactory.newVm(sbTranslation.bsMapping());
      return vm.evaluate(sbTranslation.exprBs());
    } catch (SbTranslatorExc e) {
      throw new EvaluatorExc(e.getMessage());
    } catch (InterruptedException e) {
      throw new EvaluatorExc("Evaluation process has been interrupted.");
    }
  }
}
