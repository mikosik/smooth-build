package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.PolyReferenceable;

public sealed interface SPolyReferenceable extends SReferenceable, PolyReferenceable
    permits SNamedEvaluable {}
