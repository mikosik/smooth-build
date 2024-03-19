package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.orderS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.referenceS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.schemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.valueS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.varA;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class FindValuesTest extends TestingVirtualMachine {
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
