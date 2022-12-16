package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ModuleS(
    ModFiles files,
    ImmutableBindings<TypeDefinitionS> types,
    ImmutableBindings<NamedEvaluableS> evaluables) {
}
