package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.TypeS;

public record TopRefS(TypeS type, String name, Loc loc) implements ExprS {
}
