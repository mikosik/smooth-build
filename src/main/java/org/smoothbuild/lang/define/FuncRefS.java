package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.PolyTS;

public record FuncRefS(PolyTS type, String name, Loc loc) implements PolyExprS {
}
