package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.StructType;

public record AnnotationExpression(
    StructType type, StringLiteralExpression path, boolean isPure, Location location)
    implements Expression {
}
