package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;

/**
 * Smooth module containing type, function and value definitions.
 * @param localScope scope with module members
 * @param fullScope scope with module and imported module members
 */
public record SModule(SScope localScope, SScope fullScope) {
  public String toSourceCode() {
    return typesAsSourceCode().addAll(evaluablesAsSourceCode()).toString("", "\n", "\n");
  }

  private List<String> typesAsSourceCode() {
    return listOfAll(localScope.types().toMap().values()).map(STypeDefinition::toSourceCode);
  }

  private List<String> evaluablesAsSourceCode() {
    return listOfAll(localScope.evaluables().toMap().values()).map(SEvaluable::toSourceCode);
  }
}
