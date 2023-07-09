package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.vm.bytecode.BytecodeF;

import jakarta.inject.Inject;

public class SbTranslatorFacade {
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public SbTranslatorFacade(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public SbTranslation translate(
      ImmutableBindings<NamedEvaluableS> evaluables, List<? extends ExprS> exprs) {
    var sbTranslator = new SbTranslator(bytecodeF, fileLoader, bytecodeLoader, evaluables);
    var exprBs = map(exprs, sbTranslator::translateExpr);
    var bsMapping = sbTranslator.bsMapping();
    return new SbTranslation(exprBs, bsMapping);
  }
}
