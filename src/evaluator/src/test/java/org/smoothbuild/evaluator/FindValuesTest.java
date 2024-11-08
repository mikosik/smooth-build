package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class FindValuesTest extends FrontendCompilerTestContext {
  @Test
  void find_evaluable() {
    var schemaS = sSchema(sArrayType(sIntType()));
    var valueS = sValue(schemaS, "myValue", sOrder(sIntType()));
    var scopeS = new SScope(immutableBindings(), immutableBindings(map(valueS.name(), valueS)));

    var exprs = new FindValues().execute(scopeS, list(valueS.name()));

    var referenceS = sReference(schemaS, "myValue", commandLineLocation());
    assertThat(exprs.result().get().get()).isEqualTo(list(sInstantiate(referenceS)));
  }

  @Test
  void find_polymorphic_evaluable_fails() {
    var value = sValue(sSchema(sArrayType(varA())), "myValue", sOrder(varA()));
    var scopeS = new SScope(immutableBindings(), immutableBindings(map(value.name(), value)));

    var exprs = new FindValues().execute(scopeS, list(value.name()));

    assertThat(exprs.report().logs())
        .containsExactly(error("`myValue` cannot be calculated as it is a polymorphic value."));
  }
}
