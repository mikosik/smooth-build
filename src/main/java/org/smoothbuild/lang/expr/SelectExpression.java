package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.Type;

public record SelectExpression(Type type, int index, Expression expression, Location location)
    implements Expression {
}
