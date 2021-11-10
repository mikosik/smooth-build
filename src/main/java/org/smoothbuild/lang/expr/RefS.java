package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record RefS(TypeS type, String name, Location location) implements ExprS {
}