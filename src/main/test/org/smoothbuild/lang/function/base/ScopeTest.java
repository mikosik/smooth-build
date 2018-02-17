package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.function.base.Scope.scope;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import java.util.NoSuchElementException;

import org.junit.Test;

public class ScopeTest {
  private final String name = "name";
  private Scope<String> scope;
  private Scope<String> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() throws Exception {
    given(scope = scope());
    when(() -> scope.contains(name));
    thenReturned(false);
  }

  @Test
  public void getting_not_added_binding_throws_exception() throws Exception {
    given(scope = scope());
    when(() -> scope.get(name));
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_added_binding() throws Exception {
    given(scope = scope());
    given(scope).add(name, "bound");
    when(() -> scope.contains(name));
    thenReturned(true);
  }

  @Test
  public void added_binding_can_be_retrieved() throws Exception {
    given(scope = scope());
    given(scope).add(name, "bound");
    when(() -> scope.get(name));
    thenReturned("bound");
  }

  @Test
  public void adding_same_binding_twice_throws_exception() throws Exception {
    given(scope = scope());
    given(scope).add(name, "bound");
    when(() -> scope.add(name, "bound"));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned()
      throws Exception {
    given(outerScope = scope());
    given(outerScope).add(name, "bound");
    given(scope = scope(outerScope));
    when(() -> scope.get(name));
    thenReturned("bound");
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope()
      throws Exception {
    given(outerScope = scope());
    given(outerScope).add(name, "bound in outer");
    given(scope = scope(outerScope));
    given(scope).add(name, "bound in inner");
    when(() -> scope.get(name));
    thenReturned("bound in inner");
  }

  @Test
  public void top_level_scope_doesnt_have_outer_scope() throws Exception {
    given(scope = scope());
    when(() -> scope.outerScope());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void inner_scope_can_return_outer_scope() throws Exception {
    given(outerScope = scope());
    given(scope = scope(outerScope));
    when(() -> scope.outerScope());
    thenReturned(same(outerScope));
  }
}
