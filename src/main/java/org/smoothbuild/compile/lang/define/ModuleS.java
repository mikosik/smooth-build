package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.bindings.FlatBindings;

public record ModuleS(
    ModFiles files,
    FlatBindings<TypeDefinitionS> types,
    FlatBindings<NamedEvaluableS> evaluables) {
}
