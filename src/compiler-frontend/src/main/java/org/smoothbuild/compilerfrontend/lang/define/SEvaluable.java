package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasSchemaAndLocation;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends HasSchemaAndLocation
    permits SFunc, SNamedEvaluable, SValue {}
