package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public interface Referenceable extends IdentifiableCode {
  public SSchema schema();
}
