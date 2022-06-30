package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Nameable;
import org.smoothbuild.util.collect.Nameables;
import org.smoothbuild.util.collect.Named;

public class NameBindingsTest {
  private NameBindings<Elem> inner;
  private NameBindings<Elem> outer;

  @Test
  public void empty_name_binding_doesnt_contain_any_binding() {
    inner = new NameBindings<>(aMap());
    assertThat(inner.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    inner = new NameBindings<>(aMap());
    assertCall(() -> inner.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    inner = new NameBindings<>(aMap(elem("name", 7)));
    assertThat(inner.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    inner = new NameBindings<>(aMap(elem("name", 7)));
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outer = new NameBindings<>(aMap(elem("name", 7)));
    inner = new NameBindings<>(outer, aMap());
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outer = new NameBindings<>(aMap(elem("name", 3)));
    inner = new NameBindings<>(outer, aMap(elem("name", 7)));
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void to_string() {
    outer = new NameBindings<>(aMap(elem("value-a", 7), elem("value-b", 8)));
    inner = new NameBindings<>(outer, aMap(elem("value-c", 9), elem("value-d", 10)));
    assertThat(inner.toString())
        .isEqualTo("""
            Elem[name=value-a, value=7]
            Elem[name=value-b, value=8]
              Elem[name=value-c, value=9]
              Elem[name=value-d, value=10]""");
  }

  private static Elem elem(String name, int value) {
    return new Elem(name, value);
  }

  public static <E extends Nameable> Map<String, E> aMap(E... nameables) {
    return Nameables.toMap(Arrays.asList(nameables));
  }
  private static record Elem(String name, Integer value) implements Named {}
}
