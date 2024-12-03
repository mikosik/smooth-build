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
    var sSchema = sSchema(sArrayType(sIntType()));
    var sValue = sValue(sSchema, "myValue", sOrder(sIntType()));
    var sScope = new SScope(immutableBindings(), immutableBindings(map(sValue.name(), sValue)));

    var exprs = new FindValues().execute(sScope, list(sValue.name()));

    var sReference = sReference(sSchema, "myValue", commandLineLocation());
    assertThat(exprs.result().get().get()).isEqualTo(list(sInstantiate(sReference)));
  }

  @Test
  void find_polymorphic_evaluable_fails() {
    var value = sValue(sSchema(sArrayType(varA())), "myValue", sOrder(varA()));
    var sScope = new SScope(immutableBindings(), immutableBindings(map(value.name(), value)));

    var exprs = new FindValues().execute(sScope, list(value.name()));

    assertThat(exprs.report().logs())
        .containsExactly(error("`myValue` cannot be calculated as it is a polymorphic value."));
  }
}
