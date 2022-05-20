package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.PolyTS;

public record PolyRefS(PolyTS type, String name, Loc loc) implements PolyExprS {
}
