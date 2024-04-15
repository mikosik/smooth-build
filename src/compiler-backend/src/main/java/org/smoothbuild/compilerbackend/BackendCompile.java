package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerbackend.BackendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerbackend.CompiledExprs.compilationResult;

import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;

public class BackendCompile
    implements TryFunction2<List<SExpr>, ImmutableBindings<SNamedEvaluable>, CompiledExprs> {
  private final BytecodeFactory bytecodeFactory;
  private final FileContentReader fileContentReader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public BackendCompile(
      BytecodeFactory bytecodeFactory,
      FileContentReader fileContentReader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeFactory = bytecodeFactory;
    this.fileContentReader = fileContentReader;
    this.bytecodeLoader = bytecodeLoader;
  }

  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "generateBytecode");
  }

  @Override
  public Try<CompiledExprs> apply(
      List<SExpr> sExprs, ImmutableBindings<SNamedEvaluable> evaluables) {
    var sbTranslator =
        new SbTranslator(bytecodeFactory, fileContentReader, bytecodeLoader, evaluables);
    try {
      var exprBs = sExprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return success(compilationResult(sExprs, exprBs, bsMapping));
    } catch (SbTranslatorException e) {
      return failure(fatal(e.getMessage()));
    }
  }
}
