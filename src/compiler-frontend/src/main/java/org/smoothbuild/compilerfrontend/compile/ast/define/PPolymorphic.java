package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasNameTextAndLocationImpl;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public abstract sealed class PPolymorphic extends HasNameTextAndLocationImpl
    implements HasIdAndLocation permits PLambda, PReference {
  private Id id;

  public PPolymorphic(String nameText, Location location) {
    super(nameText, location);
  }

  public abstract SSchema sSchema();

  public void setId(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }
}
