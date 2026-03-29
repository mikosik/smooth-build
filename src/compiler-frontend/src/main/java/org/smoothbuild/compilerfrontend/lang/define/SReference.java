package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public interface SReference extends HasLocation {
  public Id referencedId();
}
