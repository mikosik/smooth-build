package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInstantiate;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sOrder;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sReference;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class FindValuesTest {
  @Test
  void find_evaluable() {
    var schemaS = sSchema(sArrayType(sIntType()));
    var valueS = sValue(schemaS, "myValue", sOrder(sIntType()));
    var scopeS = new SScope(immutableBindings(), immutableBindings(map(valueS.name(), valueS)));

    var exprs = new FindValues().apply(scopeS, list(valueS.name()));

    var referenceS = sReference(schemaS, "myValue", commandLineLocation());
    assertThat(exprs).isEqualTo(success(list(sInstantiate(referenceS))));
  }

  @Test
  void find_polymorphic_evaluable_fails() {
    var value = sValue(sSchema(sArrayType(varA())), "myValue", sOrder(varA()));
    var scopeS = new SScope(immutableBindings(), immutableBindings(map(value.name(), value)));

    var exprs = new FindValues().apply(scopeS, list(value.name()));

    assertThat(exprs.logs())
        .containsExactly(error("`myValue` cannot be calculated as it is a polymorphic value."));
  }
}
