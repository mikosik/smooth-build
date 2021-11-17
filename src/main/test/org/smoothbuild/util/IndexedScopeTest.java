package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IndexedScopeTest {
  private IndexedScope<String> innerScope;
  private IndexedScope<String> outerScope;

  @Nested
  class _get_by_index {
    @Test
    public void returns_element_from_inner_scope_when_index_lower_or_equal_inner_scope_size() {
      innerScope = new IndexedScope<>(list("0", "1"));
      assertThat(innerScope.get(0))
          .isEqualTo("0");
      assertThat(innerScope.get(1))
          .isEqualTo("1");
    }

    @Test
    public void returns_element_from_outer_scope_when_index_greater_than_inner_scope_size() {
      outerScope = new IndexedScope<>(list("1", "2"));
      innerScope = new IndexedScope<>(outerScope, list("0"));
      assertThat(innerScope.get(2))
          .isEqualTo("2");
    }

    @Test
    public void throws_exception_when_index_equals_scope_total_size() {
      outerScope = new IndexedScope<>(list("1", "2"));
      innerScope = new IndexedScope<>(outerScope, list("0"));
      assertCall(() -> innerScope.get(3))
          .throwsException(IndexOutOfBoundsException.class);
    }

    @Test
    public void throws_exception_when_index_greater_than_scope_total_size() {
      outerScope = new IndexedScope<>(list("1", "2"));
      innerScope = new IndexedScope<>(outerScope, list("0"));
      assertCall(() -> innerScope.get(4))
          .throwsException(IndexOutOfBoundsException.class);
    }
  }

  @Test
  public void to_string() {
    outerScope = new IndexedScope<>(list("2", "3"));
    innerScope = new IndexedScope<>(outerScope, list("0", "1"));
    assertThat(innerScope.toString())
        .isEqualTo("""
            2
            3
              0
              1""");
  }
}
