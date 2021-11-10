package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;

import com.google.common.collect.ImmutableList;

public record OrderS(
    ArrayTypeS type, ImmutableList<ExprS> elements, Location location)
    implements ExprS {
}