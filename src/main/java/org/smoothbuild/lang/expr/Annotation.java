package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;

public record Annotation(StringLiteralExpression path, boolean isPure, Location location) {
}
