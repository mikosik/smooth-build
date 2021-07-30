package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public record ReferenceExpression(String name, Type type, Location location) implements Expression {
}
