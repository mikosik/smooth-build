package org.smoothbuild.eval;

import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.eval.compile.Compiler;
import org.smoothbuild.eval.compile.CompilerProv;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.run.QuitExc;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.VmProv;

import com.google.common.collect.ImmutableMap;

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

  public Optional<ImmutableMap<TopRefS, ValB>> evaluate(DefsS defs, List<TopRefS> values) {
    reporter.startNewPhase("Compiling");
    var compiler = compilerProv.get(defs);
    var exprs = compile(values, compiler);
    if (exprs.isEmpty()) {
      return Optional.empty();
    }

    reporter.startNewPhase("Evaluating");
    var vm = vmProv.get(compiler.nals());
    return evaluate(vm, exprs);
  }

  private Optional<ImmutableMap<TopRefS, ValB>> evaluate(
      Vm vm, Optional<ImmutableMap<TopRefS, ObjB>> exprs) {
    try {
      return vm.evaluate(exprs.get());
    } catch (InterruptedException e) {
      reporter.printlnRaw("Evaluation process has been interrupted.");
      return Optional.empty();
    }
  }

  private Optional<ImmutableMap<TopRefS, ObjB>> compile(List<TopRefS> values,
      Compiler compiler) {
    try {
      return Optional.of(toMap(values, compiler::compileExpr));
    } catch (QuitExc e) {
      reporter.printlnRawFatal(e.getMessage());
      return Optional.empty();
    }
  }
}
