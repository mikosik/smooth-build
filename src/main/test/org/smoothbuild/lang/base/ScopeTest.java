package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class ScopeTest {
  private Scope<String> scope;
  private Scope<String> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    scope = scope();
    assertThat(scope.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    scope = scope();
    assertCall(() -> scope.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_added_binding() {
    scope = scope();
    scope.add("name", "bound");
    assertThat(scope.contains("name"))
        .isTrue();
  }

  @Test
  public void scope_contains_replaced_binding_when_it_was_not_present_before_replacement() {
    scope = scope();
    scope.addOrReplace("name", "bound");
    assertThat(scope.contains("name"))
        .isTrue();
  }

  @Test
  public void scope_contains_replaced_binding_when_it_was_present_before_replacement() {
    scope = scope();
    scope.add("name", "bound");
    scope.addOrReplace("name", "bound");
    assertThat(scope.contains("name"))
        .isTrue();
  }

  @Test
  public void added_binding_can_be_retrieved() {
    scope = scope();
    scope.add("name", "bound");
    assertThat(scope.get("name"))
        .isEqualTo("bound");
  }

  @Test
  public void replaced_binding_can_be_retrieved_when_it_was_not_present_before_replacement() {
    scope = scope();
    scope.addOrReplace("name", "bound");
    assertThat(scope.get("name"))
        .isEqualTo("bound");
  }

  @Test
  public void replaced_binding_can_be_retrieved_when_it_was_present_before_replacement() {
    scope = scope();
    scope.add("name", "bound");
    scope.addOrReplace("name", "bound2");
    assertThat(scope.get("name"))
        .isEqualTo("bound2");
  }

  @Test
  public void adding_same_binding_twice_throws_exception() {
    scope = scope();
    scope.add("name", "bound");
    assertCall(() -> scope.add("name", "bound"))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outerScope = scope();
    outerScope.add("name", "bound");
    scope = scope(outerScope);
    assertThat(scope.get("name"))
        .isEqualTo("bound");
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outerScope = scope();
    outerScope.add("name", "bound in outer");
    scope = scope(outerScope);
    scope.add("name", "bound in inner");
    assertThat(scope.get("name"))
        .isEqualTo("bound in inner");
  }

  @Test
  public void top_level_scope_doesnt_have_outer_scope() {
    scope = scope();
    assertCall(() -> scope.outerScope())
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void inner_scope_can_return_outer_scope() {
    outerScope = scope();
    scope = scope(outerScope);
    assertThat(scope.outerScope())
        .isSameInstanceAs(outerScope);
  }

  @Test
  public void outer_scope_is_not_overwritten_by_adding_binding_in_inner_scope() {
    outerScope = scope();
    scope = scope(outerScope);
    scope.add("name", "binding");
    assertThat(outerScope.contains("name"))
        .isFalse();
  }

  @Test
  public void outer_scope_is_not_overwritten_by_replacing_binding_in_inner_scope() {
    outerScope = scope();
    scope = scope(outerScope);
    scope.addOrReplace("name", "binding");
    assertThat(outerScope.contains("name"))
        .isFalse();
  }

  @Test
  public void to_string() {
    outerScope = scope();
    outerScope.add("valueAbc", "abc");
    outerScope.add("valueDef", "def");
    scope = scope(outerScope);
    scope.add("valueGhi", "ghi");
    scope.add("valueJkl", "jkl");
    assertThat(scope.toString())
        .isEqualTo("""
            valueAbc=abc
            valueDef=def
              valueGhi=ghi
              valueJkl=jkl""");
  }
}
