package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * This class is immutable.
 */
public final class SBlobType extends SBaseType {
  public SBlobType() {
    super(Fqn.fqn("Blob"));
  }
}
