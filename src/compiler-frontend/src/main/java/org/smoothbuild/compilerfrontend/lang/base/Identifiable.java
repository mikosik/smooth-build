package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;

/**
 * Interface marking classes that have Id and Location.
 */
public interface Identifiable extends HasLocation {
  public Id id();
}
