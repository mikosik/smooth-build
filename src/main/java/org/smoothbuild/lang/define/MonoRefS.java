package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;

public record MonoRefS(MonoTS type, String name, Loc loc) implements MonoExprS {
}
