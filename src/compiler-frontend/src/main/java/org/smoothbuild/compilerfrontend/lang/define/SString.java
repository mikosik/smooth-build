package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SStringType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public record SString(SStringType type, String string, Location location) implements SConstant {
  @Override
  public String toSourceCode(SVarSet localVars) {
    return "\"" + string + "\"";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SString")
        .addField("type", type)
        .addField("string", string)
        .addField("location", location)
        .toString();
  }
}
