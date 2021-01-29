package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.expr.Expression;

public record DefinedBody(Expression expression) implements Body {
}
