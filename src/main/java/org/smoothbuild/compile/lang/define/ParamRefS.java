package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

public record ParamRefS(TypeS evalT, String paramName, Loc loc) implements OperS {
}
