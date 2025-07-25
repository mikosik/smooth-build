package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.Evaluable;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface PEvaluable extends Evaluable, IdentifiableCode, PContainer
    permits PFunc, PNamedEvaluable {
  public String nameText();

  public abstract PType evaluationType();

  @Override
  public SType type();

  public Maybe<PExpr> body();

  public String q();
}
