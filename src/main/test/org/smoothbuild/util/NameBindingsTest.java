package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Named;

public class NameBindingsTest {
  private NameBindings<Elem> inner;
  private NameBindings<Elem> outer;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    inner = new NameBindings<>(nList());
    assertThat(inner.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    inner = new NameBindings<>(nList());
    assertCall(() -> inner.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    inner = new NameBindings<>(nList(elem("name", 7)));
    assertThat(inner.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    inner = new NameBindings<>(nList(elem("name", 7)));
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outer = new NameBindings<>(nList(elem("name", 7)));
    inner = new NameBindings<>(outer, nList());
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outer = new NameBindings<>(nList(elem("name", 3)));
    inner = new NameBindings<>(outer, nList(elem("name", 7)));
    assertThat(inner.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void to_string() {
    outer = new NameBindings<>(nList(elem("value-a", 7), elem("value-b", 8)));
    inner = new NameBindings<>(outer, nList(elem("value-c", 9), elem("value-d", 10)));
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

  private static record Elem(String name, Integer value) implements Named {}
}
