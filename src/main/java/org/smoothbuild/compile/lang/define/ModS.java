package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ModS(
    ModPath path,
    ModFiles files,
    ImmutableBindings<TDefS> tDefs,
    ImmutableBindings<PolyRefableS> refables) {
}
