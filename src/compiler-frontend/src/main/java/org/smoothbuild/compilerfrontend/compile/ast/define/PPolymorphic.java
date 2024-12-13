package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.PBase;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public abstract sealed class PPolymorphic extends PBase permits PLambda, PReference {
  public PPolymorphic(String nameText, Location location) {
    super(nameText, location);
  }

  public abstract SSchema sSchema();
}
