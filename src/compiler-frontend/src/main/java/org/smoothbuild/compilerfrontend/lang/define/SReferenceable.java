package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasSchema;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends HasSchema, HasIdAndLocation
    permits SItem, SNamedEvaluable {}
