package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.compile.Compiler;
import org.smoothbuild.compile.CompilerExc;
import org.smoothbuild.compile.CompilerProv;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.VmProv;

import com.google.common.collect.ImmutableList;

public class Evaluator {
  private final CompilerProv compilerProv;
  private final VmProv vmProv;
  private final Reporter reporter;

  @Inject
  public Evaluator(CompilerProv compilerProv, VmProv vmProv, Reporter reporter) {
    this.compilerProv = compilerProv;
    this.vmProv = vmProv;
    this.reporter = reporter;
  }

  public Optional<ImmutableList<ValB>> evaluate(DefsS defs, List<? extends ExprS> values) {
    reporter.startNewPhase("Compiling");
    var compiler = compilerProv.get(defs);
    var exprs = compile(values, compiler);
    if (exprs.isEmpty()) {
      return Optional.empty();
    }

    reporter.startNewPhase("Evaluating");
    var vm = vmProv.get(compiler.nals());
    return evaluate(vm, exprs.get());
  }

  private Optional<ImmutableList<ValB>> evaluate(Vm vm, ImmutableList<ObjB> exprs) {
    try {
      return vm.evaluate(exprs);
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Optional.empty();
    }
  }

  private Optional<ImmutableList<ObjB>> compile(List<? extends ExprS> values, Compiler compiler) {
    try {
      return Optional.of(map(values, compiler::compileExpr));
    } catch (CompilerExc e) {
      reporter.report(fatal(e.getMessage()));
      return Optional.empty();
    }
  }
}
