package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Interface marking classes that have FQN (fully qualified name) and Location.
 */
public interface Identifiable extends HasName, HasLocation {
  public Fqn fqn();

  @Override
  public default Name name() {
    return fqn().parts().getLast();
  }
}
