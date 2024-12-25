package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends HasSchema, HasLocation
    permits SFunc, SNamedEvaluable, SValue {}
