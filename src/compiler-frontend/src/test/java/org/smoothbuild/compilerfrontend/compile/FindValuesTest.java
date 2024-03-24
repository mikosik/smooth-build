package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.orderS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.referenceS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.schemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.valueS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

public class FindValuesTest {
  @Test
  void find_evaluable() {
    var schemaS = schemaS(arrayTS(intTS()));
    var valueS = valueS(schemaS, "myValue", orderS(intTS()));
    var scopeS = new ScopeS(immutableBindings(), immutableBindings(map(valueS.name(), valueS)));

    var exprs = new FindValues().apply(scopeS, list(valueS.name()));

    var referenceS = referenceS(schemaS, "myValue", commandLineLocation());
    assertThat(exprs).isEqualTo(success(list(instantiateS(referenceS))));
  }

  @Test
  void find_polymorphic_evaluable_fails() {
    var value = valueS(schemaS(arrayTS(varA())), "myValue", orderS(varA()));
    var scopeS = new ScopeS(immutableBindings(), immutableBindings(map(value.name(), value)));

    var exprs = new FindValues().apply(scopeS, list(value.name()));

    assertThat(exprs.logs())
        .containsExactly(error("`myValue` cannot be calculated as it is a polymorphic value."));
  }
}
