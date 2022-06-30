package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Named;

public class ScopeTest {
  private Scope<Elem> innerScope;
  private Scope<Elem> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    innerScope = new Scope<>(nList());
    assertThat(innerScope.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    innerScope = new Scope<>(nList());
    assertCall(() -> innerScope.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    innerScope = new Scope<>(nList(elem("name", 7)));
    assertThat(innerScope.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    innerScope = new Scope<>(nList(elem("name", 7)));
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outerScope = new Scope<>(nList(elem("name", 7)));
    innerScope = new Scope<>(outerScope, nList());
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outerScope = new Scope<>(nList(elem("name", 3)));
    innerScope = new Scope<>(outerScope, nList(elem("name", 7)));
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void to_string() {
    outerScope = new Scope<>(nList(elem("value-a", 7), elem("value-b", 8)));
    innerScope = new Scope<>(outerScope, nList(elem("value-c", 9), elem("value-d", 10)));
    assertThat(innerScope.toString())
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
