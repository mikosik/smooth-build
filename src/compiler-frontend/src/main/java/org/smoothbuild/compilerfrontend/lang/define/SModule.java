package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

/**
 * Smooth module containing type, function and value definitions.
 */
public record SModule(
    Bindings<STypeDefinition> types, Bindings<SNamedEvaluable> evaluables, SScope fullScope) {
  public String toSourceCode() {
    return typesAsSourceCode().addAll(evaluablesAsSourceCode()).toString("", "\n", "\n");
  }

  private List<String> typesAsSourceCode() {
    return listOfAll(types.toMap().values()).map(STypeDefinition::toSourceCode);
  }

  private List<String> evaluablesAsSourceCode() {
    return listOfAll(evaluables.toMap().values()).map(SEvaluable::toSourceCode);
  }
}
