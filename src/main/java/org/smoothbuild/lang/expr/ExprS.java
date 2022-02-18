package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.Nal;
import org.smoothbuild.lang.type.impl.TypeS;

/**
 * Expression in smooth language.
 */
public sealed interface ExprS extends Nal
    permits BlobS, CallS, IntS, OrderS, ParamRefS, SelectS, StringS, TopRefS {
  public TypeS type();

  @Override
  public String name();

  @Override
  public Loc loc();
}
