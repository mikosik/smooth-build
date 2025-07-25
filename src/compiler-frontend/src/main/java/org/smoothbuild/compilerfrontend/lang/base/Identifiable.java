package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Interface marking classes that have FQN (fully qualified name).
 */
public interface Identifiable extends HasName {
  public Fqn fqn();

  @Override
  public default Name name() {
    return fqn().last();
  }
}
