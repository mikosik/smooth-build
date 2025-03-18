package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

public record SScope(Bindings<STypeDefinition> types, Bindings<SPolyEvaluable> evaluables) {
  @Override
  public String toString() {
    return new ToStringBuilder("SScope")
        .addListField("types", list(types))
        .addListField("evaluables", list(evaluables))
        .toString();
  }
}
