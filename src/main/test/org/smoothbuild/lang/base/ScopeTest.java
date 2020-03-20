package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.NoSuchElementException;

import org.junit.Test;

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
  public void added_binding_can_be_retrieved() {
    scope = scope();
    scope.add("name", "bound");
    assertThat(scope.get("name"))
        .isEqualTo("bound");
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
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void inner_scope_can_return_outer_scope() {
    outerScope = scope();
    scope = scope(outerScope);
    assertThat(scope.outerScope())
        .isSameInstanceAs(outerScope);
  }
}
