package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.compile.lang.define.ValS;

public class SbTranslatorFacade {
  private final Provider<SbTranslator> sbTranslatorProv;

  @Inject
  public SbTranslatorFacade(Provider<SbTranslator> sbTranslatorProv) {
    this.sbTranslatorProv = sbTranslatorProv;
  }

  public SbTranslation translate(List<ValS> vals) {
    var sbTranslator = sbTranslatorProv.get();
    var exprBs = map(vals, sbTranslator::translateEvaluable);
    var bsMapping = sbTranslator.bsMapping();
    return new SbTranslation(exprBs, bsMapping);
  }
}
