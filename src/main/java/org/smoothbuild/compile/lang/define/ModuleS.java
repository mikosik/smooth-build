package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.bindings.SingleScopeBindings;

public record ModuleS(
    ModFiles files,
    SingleScopeBindings<TypeDefinitionS> types,
    SingleScopeBindings<NamedEvaluableS> evaluables) {
}
