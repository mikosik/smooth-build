package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ModuleS(
    ModPath path,
    ModFiles files,
    ImmutableBindings<TDefS> tDefs,
    ImmutableBindings<PolyEvaluableS> evaluables) {
}
