package org.smoothbuild.lang.obj;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.TypeS;

public record ObjRefS(TypeS type, String name, Loc loc) implements ExprS {
}
