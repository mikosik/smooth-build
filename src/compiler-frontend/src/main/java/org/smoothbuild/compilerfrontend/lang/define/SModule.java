package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Smooth module containing type, function and value definitions.
 */
public record SModule(
    Map<Name, STypeDefinition> types, Map<Name, SPolyEvaluable> evaluables, SScope scope) {
  public String toSourceCode() {
    return typesAsSourceCode().addAll(evaluablesAsSourceCode()).toString("", "\n", "\n");
  }

  private List<String> typesAsSourceCode() {
    return listOfAll(types.values()).map(STypeDefinition::toSourceCode);
  }

  private List<String> evaluablesAsSourceCode() {
    return listOfAll(evaluables.values()).map(SPolyEvaluable::toSourceCode);
  }
}
