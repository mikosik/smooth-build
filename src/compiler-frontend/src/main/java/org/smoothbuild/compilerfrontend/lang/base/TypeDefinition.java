package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SType;

public interface TypeDefinition extends Identifiable {
  public SType type();
}
