package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.base.Id;

/**
 * This class is immutable.
 */
public final class SBlobType extends SBaseType {
  public SBlobType() {
    super(Id.id("Blob"));
  }
}
