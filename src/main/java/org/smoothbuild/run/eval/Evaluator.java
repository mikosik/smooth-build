package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.compile.ConvertSbExc;
import org.smoothbuild.compile.SbConverter;
import org.smoothbuild.compile.SbConverterProv;
import org.smoothbuild.lang.define.ExprS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.VmProv;

import com.google.common.collect.ImmutableList;

public class Evaluator {
  private final SbConverterProv sbConverterProv;
  private final VmProv vmProv;
  private final Reporter reporter;

  @Inject
  public Evaluator(SbConverterProv sbConverterProv, VmProv vmProv, Reporter reporter) {
    this.sbConverterProv = sbConverterProv;
    this.vmProv = vmProv;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<CnstB>> evaluate(List<? extends ExprS> exprsS) {
    reporter.startNewPhase("Compiling");
    var sbCoverter = sbConverterProv.get();
    var exprsB = convert(exprsS, sbCoverter);
    if (exprsB.isEmpty()) {
      return Optional.empty();
    }

    reporter.startNewPhase("Evaluating");
    var vm = vmProv.get(sbCoverter.nals());
    return evaluate(vm, exprsB.get());
  }

  private Optional<ImmutableList<CnstB>> evaluate(Vm vm, ImmutableList<ObjB> exprs) {
    try {
      return vm.evaluate(exprs);
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Optional.empty();
    }
  }

  private Optional<ImmutableList<ObjB>> convert(
      List<? extends ExprS> exprsS, SbConverter sbConverter) {
    try {
      return Optional.of(map(exprsS, sbConverter::convertExpr));
    } catch (ConvertSbExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }
}
