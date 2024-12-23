package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.name.Id;

/**
 * Interface for classes that have Id.
 */
public interface HasId {
  public Id id();

  public default String q() {
    return id().q();
  }
}
