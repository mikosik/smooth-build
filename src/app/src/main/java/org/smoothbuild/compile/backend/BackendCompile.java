package org.smoothbuild.compile.backend;

import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.util.function.Function;

import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.load.BytecodeLoader;
import org.smoothbuild.vm.bytecode.load.FileLoader;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import jakarta.inject.Inject;

public class BackendCompile implements
    Function<Tuple2<Array<ExprS>, ImmutableBindings<NamedEvaluableS>>,
            Maybe<Tuple2<Array<ExprB>, BsMapping>>> {
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public BackendCompile(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  @Override
  public Maybe<Tuple2<Array<ExprB>, BsMapping>> apply(
      Tuple2<Array<ExprS>, ImmutableBindings<NamedEvaluableS>> argument) {
    Array<ExprS> exprs = argument._1();
    var evaluables = argument._2();
    var sbTranslator = new SbTranslator(bytecodeF, fileLoader, bytecodeLoader, evaluables);
    try {
      var exprBs = exprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return maybe(Tuple.of(exprBs, bsMapping));
    } catch (SbTranslatorExc e) {
      return maybeLogs(fatal(e.getMessage()));
    }
  }
}
