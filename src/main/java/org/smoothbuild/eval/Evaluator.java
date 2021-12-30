package org.smoothbuild.eval;

import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.eval.compile.CompilerProv;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.vm.VmProv;

public class Evaluator {
  private final CompilerProv compilerProv;
  private final VmProv vmProv;

  @Inject
  public Evaluator(CompilerProv compilerProv, VmProv vmProv) {
    this.compilerProv = compilerProv;
    this.vmProv = vmProv;
  }

  public Map<TopRefS, Optional<ObjB>> evaluate(DefsS defs, List<TopRefS> values)
      throws InterruptedException {
    var compiler = compilerProv.get(defs);
    var exprsB = toMap(values, compiler::compileExpr);
    var vm = vmProv.get(compiler.nals());
    return vm.evaluate(exprsB);
  }
}
