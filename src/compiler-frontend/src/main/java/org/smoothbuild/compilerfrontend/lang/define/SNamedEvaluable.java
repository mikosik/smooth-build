package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasSchema;

/**
 * Evaluable that has fully qualified name.
 */
public sealed interface SNamedEvaluable
    extends HasSchema, SEvaluable, SReferenceable, HasIdAndLocation
    permits SNamedFunc, SNamedValue {}
