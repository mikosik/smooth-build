package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.PolyReferenceable;

public sealed interface PPolyReferenceable extends PReferenceable, PolyReferenceable
    permits PNamedEvaluable {}
