package org.smoothbuild.compilerfrontend.lang.base;

/**
 * Interface for classes that have Id.
 */
public interface HasId {
  public Id id();

  public default String q() {
    return id().q();
  }
}
