package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.compilerfrontend.lang.base.DefaultValue;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class SDefaultValue implements DefaultValue {
  private final Fqn fqn;

  public SDefaultValue(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SDefaultValue that && Objects.equals(fqn, that.fqn);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fqn);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SDefaultValue").addField("fqn", fqn()).toString();
  }
}
