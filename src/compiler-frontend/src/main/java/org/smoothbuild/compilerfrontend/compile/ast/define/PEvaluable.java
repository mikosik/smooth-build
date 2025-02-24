package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface PEvaluable extends IdentifiableCode, PContainer
    permits PFunc, PNamedEvaluable {
  public String nameText();

  public PTypeParams typeParams();

  public abstract PType evaluationType();

  public SType sType();

  public SSchema schema();

  public Maybe<PExpr> body();

  public String q();
}
