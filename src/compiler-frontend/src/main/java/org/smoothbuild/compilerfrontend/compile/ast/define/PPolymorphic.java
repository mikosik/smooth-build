package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public sealed interface PPolymorphic extends Identifiable permits PLambda, PReference {
  public abstract SSchema schema();
}
