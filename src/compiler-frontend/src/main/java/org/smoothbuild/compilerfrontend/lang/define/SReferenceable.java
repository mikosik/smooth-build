package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasSchemaAndIdAndLocation;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends HasSchemaAndIdAndLocation
    permits SItem, SNamedEvaluable {}
