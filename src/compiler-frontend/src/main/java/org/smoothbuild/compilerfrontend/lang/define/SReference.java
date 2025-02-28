package org.smoothbuild.compilerfrontend.lang.define;

/**
 * Reference to SReferenceable.
 */
public sealed interface SReference extends SPolymorphic permits SMonoReference, SPolyReference {
  @Override
  public String toSourceCode();
}
