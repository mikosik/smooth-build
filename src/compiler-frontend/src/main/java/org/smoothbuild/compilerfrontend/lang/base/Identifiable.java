package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Interface marking classes that have Id and Location.
 */
public interface Identifiable extends HasName, HasLocation {
  public Id id();

  @Override
  public default Name name() {
    return id().parts().getLast();
  }
}
