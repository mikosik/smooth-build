package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

public class ScopeTest {
  private Scope<Elem> scope;
  private Scope<Elem> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    scope = new Scope<>(NamedList.empty());
    assertThat(scope.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    scope = new Scope<>(NamedList.empty());
    assertCall(() -> scope.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    scope = new Scope<>(namedList(list(elem("name", 7))));
    assertThat(scope.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    scope = new Scope<>(namedList(list(elem("name", 7))));
    assertThat(scope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outerScope = new Scope<>(namedList(list(elem("name", 7))));
    scope = new Scope<>(outerScope, NamedList.empty());
    assertThat(scope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outerScope = new Scope<>(namedList(list(elem("name", 3))));
    scope = new Scope<>(outerScope, namedList(list(elem("name", 7))));
    assertThat(scope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void top_level_scope_doesnt_have_outer_scope() {
    scope = new Scope<>(NamedList.empty());
    assertCall(() -> scope.outerScope())
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void inner_scope_can_return_outer_scope() {
    outerScope = new Scope<>(NamedList.empty());
    scope = new Scope<>(outerScope, NamedList.empty());
    assertThat(scope.outerScope())
        .isSameInstanceAs(outerScope);
  }

  @Test
  public void to_string() {
    outerScope = new Scope<>(namedList(list(elem("value-a", 7), elem("value-b", 8))));
    scope = new Scope<>(outerScope, namedList(list(elem("value-c", 9), elem("value-d", 10))));
    assertThat(scope.toString())
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
