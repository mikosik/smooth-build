package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public interface Referenceable extends Identifiable {
  public SSchema schema();
}
