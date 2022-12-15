package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class ImmutableBindingsTest extends AbstractBindingsTestSuite {
  @Test
  public void to_string() {
    var bindings = newBindings(elem("value-a", 7), elem("value-b", 8));
    assertThat(bindings.toString())
        .isEqualTo("""
              Elem[name=value-a, value=7]
              Elem[name=value-b, value=8]""");
  }

  @Override
  public ImmutableBindings<Elem> newBindings(Elem... elems) {
    return ImmutableBindings.immutableBindings(mapOfElems(elems));
  }
}
