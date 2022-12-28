package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.util.bindings.SingleScopeBindings;

public record ModuleS(
    SingleScopeBindings<TypeDefinitionS> types,
    SingleScopeBindings<NamedEvaluableS> evaluables) {
}
