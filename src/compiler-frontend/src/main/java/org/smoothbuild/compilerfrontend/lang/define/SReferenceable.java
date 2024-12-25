package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends HasIdAndLocation permits SItem, SNamedEvaluable {}
