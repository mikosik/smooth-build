package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Ial;
import org.smoothbuild.compilerfrontend.lang.base.Id;

/**
 * Referencable.
 */
public sealed interface PReferenceable extends Ial permits PItem, PNamedEvaluable {
  public String nameText();

  public void setId(Id id);
}
