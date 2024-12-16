package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;

/**
 * Referencable.
 */
public sealed interface PReferenceable extends HasIdAndLocation permits PItem, PNamedEvaluable {}
