package org.smoothbuild.compile.backend;

import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Try.failure;
import static org.smoothbuild.out.log.Try.success;

import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.load.BytecodeLoader;
import org.smoothbuild.vm.bytecode.load.FilePersister;

public class BackendCompile
    implements Function<
        Tuple2<List<ExprS>, ImmutableBindings<NamedEvaluableS>>,
        Try<Tuple2<List<ExprB>, BsMapping>>> {
  private final BytecodeF bytecodeF;
  private final FilePersister filePersister;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public BackendCompile(
      BytecodeF bytecodeF, FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.filePersister = filePersister;
    this.bytecodeLoader = bytecodeLoader;
  }

  @Override
  public Try<Tuple2<List<ExprB>, BsMapping>> apply(
      Tuple2<List<ExprS>, ImmutableBindings<NamedEvaluableS>> argument) {
    List<ExprS> exprs = argument.element1();
    var evaluables = argument.element2();
    var sbTranslator = new SbTranslator(bytecodeF, filePersister, bytecodeLoader, evaluables);
    try {
      var exprBs = exprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return success(tuple(exprBs, bsMapping));
    } catch (SbTranslatorException e) {
      return failure(fatal(e.getMessage()));
    }
  }
}
