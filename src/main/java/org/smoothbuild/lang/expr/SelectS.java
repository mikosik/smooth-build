package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectS(TypeS type, int index, ExprS expr, Location location)
    implements ExprS {
}