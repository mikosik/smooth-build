package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.name.Bindings;

public record SScope(Bindings<STypeDefinition> types, Bindings<SNamedEvaluable> evaluables) {}
