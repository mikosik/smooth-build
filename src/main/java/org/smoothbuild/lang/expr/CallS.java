package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ExprS functionExpr,
    ImmutableList<ExprS> arguments, Location location) implements ExprS {
}
