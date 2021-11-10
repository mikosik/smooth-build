package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.StringTypeS;

public record StringS(StringTypeS type, String string, Location location)
    implements ExprS {
}