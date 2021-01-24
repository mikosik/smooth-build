package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class ScopeTest {
  private Scope<String> scope;
  private Scope<String> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    scope = new Scope<>(Map.of());
    assertThat(scope.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    scope = new Scope<>(Map.of());
    assertCall(() -> scope.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    scope = new Scope<>(Map.of("name", "bound"));
    assertThat(scope.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    scope = new Scope<>(Map.of("name", "bound"));
    assertThat(scope.get("name"))
        .isEqualTo("bound");
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outerScope = new Scope<>(Map.of("name", "bound"));
    scope = new Scope<>(outerScope, Map.of());
    assertThat(scope.get("name"))
        .isEqualTo("bound");
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outerScope = new Scope<>(Map.of("name", "bound in outer"));
    scope = new Scope<>(outerScope, Map.of("name", "bound in inner"));
    assertThat(scope.get("name"))
        .isEqualTo("bound in inner");
  }

  @Test
  public void top_level_scope_doesnt_have_outer_scope() {
    scope = new Scope<>(Map.of());
    assertCall(() -> scope.outerScope())
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void inner_scope_can_return_outer_scope() {
    outerScope = new Scope<>(Map.of());
    scope = new Scope<>(outerScope, Map.of());
    assertThat(scope.outerScope())
        .isSameInstanceAs(outerScope);
  }

  @Test
  public void names_to_string() {
    outerScope = new Scope<>(ImmutableMap.of("value-a", "aaa", "value-b", "bbb"));
    scope = new Scope<>(outerScope, ImmutableMap.of("value-c", "ccc", "value-d", "ddd"));
    assertThat(scope.namesToString())
        .isEqualTo("""
            value-a
            value-b
              value-c
              value-d""");
  }

  @Test
  public void to_string() {
    outerScope = new Scope<>(ImmutableMap.of("value-a", "aaa", "value-b", "bbb"));
    scope = new Scope<>(outerScope, ImmutableMap.of("value-c", "ccc", "value-d", "ddd"));
    assertThat(scope.toString())
        .isEqualTo("""
            value-a=aaa
            value-b=bbb
              value-c=ccc
              value-d=ddd""");
  }
}
