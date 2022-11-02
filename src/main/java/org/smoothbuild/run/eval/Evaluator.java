package org.smoothbuild.run.eval;

import static org.smoothbuild.compile.lang.base.Loc.commandLineLoc;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.compile.lang.define.PolyRefS;
import org.smoothbuild.compile.lang.define.PolyValS;
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

  public Optional<ImmutableList<InstB>> evaluate(ImmutableList<PolyValS> vals) {
    try {
      var refs = map(vals, v -> new PolyRefS(v, commandLineLoc()));
      reporter.startNewPhase("Compiling");
      var sbTranslation = sbTranslatorFacade.translate(refs);
      reporter.startNewPhase("Evaluating");
      var vm = vmFactory.newVm(sbTranslation.bsMapping());
      return vm.evaluate(sbTranslation.exprBs());
    } catch (SbTranslatorExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Optional.empty();
    }
  }
}
