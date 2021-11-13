package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

public class ScopeTest {
  private Scope<Elem> innerScope;
  private Scope<Elem> outerScope;

  @Test
  public void empty_scope_doesnt_contain_any_binding() {
    innerScope = new Scope<>(NamedList.empty());
    assertThat(innerScope.contains("name"))
        .isFalse();
  }

  @Test
  public void getting_not_added_binding_throws_exception() {
    innerScope = new Scope<>(NamedList.empty());
    assertCall(() -> innerScope.get("name"))
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void scope_contains_binding_that_is_in_current_scope() {
    innerScope = new Scope<>(namedList(elem("name", 7)));
    assertThat(innerScope.contains("name"))
        .isTrue();
  }

  @Test
  public void binding_from_current_scope_can_be_retrieved() {
    innerScope = new Scope<>(namedList(elem("name", 7)));
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
    outerScope = new Scope<>(namedList(elem("name", 7)));
    innerScope = new Scope<>(outerScope, NamedList.empty());
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void binding_in_current_scope_hides_binding_from_outer_scope() {
    outerScope = new Scope<>(namedList(elem("name", 3)));
    innerScope = new Scope<>(outerScope, namedList(elem("name", 7)));
    assertThat(innerScope.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Nested
  class _index_of {
    @Test
    public void throws_exception_when_name_is_missing() {
      innerScope = new Scope<>(namedList(elem("name", 3)));
      assertCall(() -> innerScope.indexOf("other name"))
          .throwsException(NoSuchElementException.class);
    }

    @Test
    public void returns_index() {
      innerScope = new Scope<>(namedList(elem("a", 3), elem("b", 7)));
      assertThat(innerScope.indexOf("a"))
          .isEqualTo(0);
      assertThat(innerScope.indexOf("b"))
          .isEqualTo(1);
    }

    @Test
    public void returns_index_from_outer_scope_increased_by_inner_scope_elem_size() {
      outerScope = new Scope<>(namedList(elem("c", 3), elem("d", 3)));
      innerScope = new Scope<>(outerScope, namedList(elem("a", 7), elem("b", 7)));
      assertThat(innerScope.indexOf("c"))
          .isEqualTo(2);
      assertThat(innerScope.indexOf("d"))
          .isEqualTo(3);
    }

    @Test
    public void name_in_inner_scope_shadows_name_from_outer_scope() {
      outerScope = new Scope<>(namedList(elem("c", 3), elem("d", 3)));
      innerScope = new Scope<>(outerScope, namedList(elem("a", 7), elem("c", 7)));
      assertThat(innerScope.indexOf("c"))
          .isEqualTo(1);
    }
  }

  @Test
  public void to_string() {
    outerScope = new Scope<>(namedList(elem("value-a", 7), elem("value-b", 8)));
    innerScope = new Scope<>(outerScope, namedList(elem("value-c", 9), elem("value-d", 10)));
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
