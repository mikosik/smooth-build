package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;

/**
 * Reference to SReferenceable.
 */
public sealed interface SReference extends HasLocation permits SMonoReference, SPolyReference {}
