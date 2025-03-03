package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;

public sealed interface PEvaluable extends IdentifiableCode, PContainer
    permits PFunc, PNamedEvaluable {
  public String nameText();

  public PTypeParams pTypeParams();

  public abstract PType evaluationType();

  public SType sType();

  public STypeScheme typeScheme();

  public Maybe<PExpr> body();

  public String q();
}
