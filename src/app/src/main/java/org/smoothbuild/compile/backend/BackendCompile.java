package org.smoothbuild.compile.backend;

import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Try.failure;
import static org.smoothbuild.out.log.Try.success;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.load.BytecodeLoader;
import org.smoothbuild.vm.bytecode.load.FileLoader;

public class BackendCompile
    implements Function<
        Tuple2<List<ExprS>, ImmutableBindings<NamedEvaluableS>>,
        Try<Tuple2<List<ExprB>, BsMapping>>> {
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public BackendCompile(BytecodeF bytecodeF, FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  @Override
  public Try<Tuple2<List<ExprB>, BsMapping>> apply(
      Tuple2<List<ExprS>, ImmutableBindings<NamedEvaluableS>> argument) {
    List<ExprS> exprs = argument._1();
    var evaluables = argument._2();
    var sbTranslator = new SbTranslator(bytecodeF, fileLoader, bytecodeLoader, evaluables);
    try {
      var exprBs = exprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return success(Tuple.of(exprBs, bsMapping));
    } catch (SbTranslatorException e) {
      return failure(fatal(e.getMessage()));
    }
  }
}
