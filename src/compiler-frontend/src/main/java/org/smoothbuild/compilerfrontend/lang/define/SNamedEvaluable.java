package org.smoothbuild.compilerfrontend.lang.define;

/**
 * Evaluable that has fully qualified name.
 */
public sealed interface SNamedEvaluable extends SEvaluable permits SNamedFunc, SNamedValue {}
