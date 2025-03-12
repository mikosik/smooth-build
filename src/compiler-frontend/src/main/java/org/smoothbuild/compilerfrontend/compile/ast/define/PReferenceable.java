package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Referenceable;

/**
 * Referencable.
 */
public sealed interface PReferenceable extends Referenceable
    permits PMonoReferenceable, PPolyEvaluable {}
