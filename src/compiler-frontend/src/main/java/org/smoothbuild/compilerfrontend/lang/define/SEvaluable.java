package org.smoothbuild.compilerfrontend.lang.define;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends HasSchemaAndLocation
    permits SFunc, SNamedEvaluable, SValue {}
