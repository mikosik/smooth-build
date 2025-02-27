package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Referenceable;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends Referenceable
    permits SMonoReferenceable, SPolyReferenceable {}
