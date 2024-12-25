package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;

/**
 * Evaluable that has fully qualified name.
 */
public sealed interface SNamedEvaluable extends SEvaluable, SReferenceable, HasIdAndLocation
    permits SNamedFunc, SNamedValue {}
