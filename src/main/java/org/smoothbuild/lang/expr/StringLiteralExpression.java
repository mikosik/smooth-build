package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.StringSType;

public record StringLiteralExpression(StringSType type, String string, Location location)
    implements Expression {
}
