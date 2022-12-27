package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.bindings.Bindings.mutableBindings;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class MutableBindingsTest extends AbstractScopedBindingsTestSuite {
  @Override
  protected Bindings<Elem> newMapBindings(ImmutableBindings<Elem> outerScope, Elem... elems) {
    var mutableBindings = mutableBindings(outerScope);
    for (var elem : elems) {
      mutableBindings.add(elem.name(), elem);
    }
    return mutableBindings;
  }

  @Test
  public void add_return_null_if_binding_is_not_already_present() {
    var mutableBindings = new MutableBindings<String>(immutableBindings());
    assertThat(mutableBindings.add("name", "value"))
        .isEqualTo(null);
  }

  @Test
  public void add_overwrites_previous_binding_if_present() {
    var mutableBindings = new MutableBindings<String>(immutableBindings());
    mutableBindings.add("name", "value");
    mutableBindings.add("name", "value2");
    assertThat(mutableBindings.get("name"))
        .isEqualTo("value2");
  }

  @Test
  public void add_return_previous_binding_if_present() {
    var mutableBindings = new MutableBindings<String>(immutableBindings());
    mutableBindings.add("name", "value");
    assertThat(mutableBindings.add("name", "value2"))
        .isEqualTo("value");
  }
}
