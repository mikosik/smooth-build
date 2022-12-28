package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Iterables.joinToString;
import static org.smoothbuild.util.collect.Maps.override;

import org.smoothbuild.util.bindings.SingleScopeBindings;
import org.smoothbuild.util.collect.Named;

public record DefinitionsS(
    SingleScopeBindings<TypeDefinitionS> types,
    SingleScopeBindings<NamedEvaluableS> evaluables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(immutableBindings(), immutableBindings());
  }

  public DefinitionsS withModule(ModuleS module) {
    return new DefinitionsS(
        merge(types, module.types()),
        merge(evaluables, module.evaluables())
    );
  }

  public <E extends Named> SingleScopeBindings<E> merge(
      SingleScopeBindings<E> outer,
      SingleScopeBindings<E> inner) {
    return immutableBindings(override(inner.toMap(), outer.toMap()));
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "types = [",
        indent(types().toString()),
        "]",
        "evaluables = [",
        indent(evaluables.toString()),
        "]"
    );
    return "TypeDefinitionS(\n" + indent(fields) + "\n)";
  }
}
