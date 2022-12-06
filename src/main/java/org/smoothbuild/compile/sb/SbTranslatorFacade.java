package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.compile.lang.define.ExprS;

public class SbTranslatorFacade {
  private final Provider<SbTranslator> sbTranslatorProv;

  @Inject
  public SbTranslatorFacade(Provider<SbTranslator> sbTranslatorProv) {
    this.sbTranslatorProv = sbTranslatorProv;
  }

  public SbTranslation translate(List<? extends ExprS> exprs) {
    var sbTranslator = sbTranslatorProv.get();
    var exprBs = map(exprs, sbTranslator::translateExpr);
    var bsMapping = sbTranslator.bsMapping();
    return new SbTranslation(exprBs, bsMapping);
  }
}
