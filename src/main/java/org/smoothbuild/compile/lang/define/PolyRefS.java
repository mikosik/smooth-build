package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;

public record PolyRefS(PolyEvaluableS polyEvaluable, String name, Loc loc) {
}
