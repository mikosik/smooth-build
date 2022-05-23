package org.smoothbuild.lang.expr;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.Nal;
import org.smoothbuild.lang.like.Expr;
import org.smoothbuild.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed interface ExprS extends Nal, Expr
    permits BlobS, CallS, IntS, OrderS, ParamRefS, SelectS, StringS, TopRefS {
  public TypeS type();

  @Override
  public default Optional<TypeS> typeO() {
    return Optional.of(type());
  }

  @Override
  public String name();

  @Override
  public Loc loc();
}
