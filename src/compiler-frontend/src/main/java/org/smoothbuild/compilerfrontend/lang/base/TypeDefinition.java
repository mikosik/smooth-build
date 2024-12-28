package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public interface TypeDefinition extends HasLocation {
  public Id id();

  public SType type();
}
