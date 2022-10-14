package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements InstS {
}
