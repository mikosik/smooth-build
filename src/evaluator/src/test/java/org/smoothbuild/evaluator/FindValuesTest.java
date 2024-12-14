package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class FindValuesTest extends FrontendCompilerTestContext {
  @Test
  void find_evaluable() {
    var sSchema = sSchema(sIntArrayT());
    var sValue = sValue(sSchema, "myValue", sOrder(sIntType()));
    var sScope =
        new SScope(immutableBindings(), immutableBindings(map(sValue.id().full(), sValue)));

    var exprs = new FindValues().execute(sScope, list(sValue.id().full()));

    var sReference = sReference(sSchema, fqn("myValue"), commandLineLocation());
    assertThat(exprs.result().get().get()).isEqualTo(list(sInstantiate(sReference)));
  }

  @Test
  void find_polymorphic_evaluable_fails() {
    var value = sValue(sSchema(sVarAArrayT()), "myValue", sOrder(varA()));
    var sScope =
        new SScope(immutableBindings(), immutableBindings(map(value.id().full(), value)));

    var exprs = new FindValues().execute(sScope, list(value.id().full()));

    assertThat(exprs.report().logs())
        .containsExactly(error("`myValue` cannot be calculated as it is a polymorphic value."));
  }
}
