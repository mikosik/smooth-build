package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public abstract sealed class PPolymorphic extends HasLocationImpl
    implements HasIdAndLocation, HasNameText permits PLambda, PReference {
  private final String nameText;
  private Id id;

  public PPolymorphic(String nameText, Location location) {
    super(location);
    this.nameText = nameText;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  public abstract SSchema sSchema();

  public void setId(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public String q() {
    return Strings.q(nameText);
  }
}
