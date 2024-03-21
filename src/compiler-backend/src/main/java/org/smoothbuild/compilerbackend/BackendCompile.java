package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerbackend.CompiledExprs.compilationResult;

import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;

public class BackendCompile
    implements TryFunction2<List<ExprS>, ImmutableBindings<NamedEvaluableS>, CompiledExprs> {
  private final BytecodeFactory bytecodeFactory;
  private final FilePersister filePersister;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public BackendCompile(
      BytecodeFactory bytecodeFactory, FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    this.bytecodeFactory = bytecodeFactory;
    this.filePersister = filePersister;
    this.bytecodeLoader = bytecodeLoader;
  }

  @Override
  public Try<CompiledExprs> apply(
      List<ExprS> exprs, ImmutableBindings<NamedEvaluableS> evaluables) {
    var sbTranslator = new SbTranslator(bytecodeFactory, filePersister, bytecodeLoader, evaluables);
    try {
      var exprBs = exprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return success(compilationResult(exprBs, bsMapping));
    } catch (SbTranslatorException e) {
      return failure(fatal(e.getMessage()));
    }
  }
}
