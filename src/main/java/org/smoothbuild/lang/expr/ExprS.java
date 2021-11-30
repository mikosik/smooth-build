package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Expression in smooth language.
 */
public sealed interface ExprS extends Nal
    permits BlobS, CallS, IntS, OrderS, ParamRefS, RefS, StringS, SelectS {
  public TypeS type();

  @Override
  public String name();

  @Override
  public Loc loc();
}
