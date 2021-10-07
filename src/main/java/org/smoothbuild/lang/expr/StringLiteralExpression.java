package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.StringType;

public record StringLiteralExpression(StringType type, String string, Location location)
    implements Expression {
}
