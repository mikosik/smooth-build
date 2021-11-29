package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record RefS(TypeS type, String name, Loc loc) implements ExprS {
}
