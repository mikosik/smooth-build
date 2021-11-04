package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectExpression(TypeS type, int index, Expression expression, Location location)
    implements Expression {
}
