package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Ial;

/**
 * Referencable.
 */
public sealed interface PReferenceable extends Ial permits PItem, PNamedEvaluable {}
