package org.smoothbuild.compilerfrontend.lang.base;

public interface Identifiable {
  public Id id();

  public default String q() {
    return id().q();
  }
}
