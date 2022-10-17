package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.ValS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SbTranslatorFacade {
  private final Provider<SbTranslator> sbTranslatorProv;

  @Inject
  public SbTranslatorFacade(Provider<SbTranslator> sbTranslatorProv) {
    this.sbTranslatorProv = sbTranslatorProv;
  }

  public SbTranslation translate(List<ValS> vals) {
    var sbTranslator = sbTranslatorProv.get();
    var exprBs = map(vals, sbTranslator::translateExpr);
    var tagLocs = sbTranslator.tagLocs();
    return new SbTranslation(exprBs, tagLocs);
  }

  public static record SbTranslation(
      ImmutableList<ExprB> exprBs,
      ImmutableMap<ExprB, TagLoc> tagLocs) {
  }
}
