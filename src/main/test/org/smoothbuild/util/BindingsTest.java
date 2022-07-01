package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Bindings.bindings;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Nameables;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public class BindingsTest {
  private Bindings<Elem> inner;
  private MutableBindings<Elem> mutable;
  private Bindings<Elem> outer;

  @Nested
  class _immutable_bindings {
    @Test
    public void empty_name_binding_doesnt_contain_any_binding() {
      inner = bindings(aMap());
      assertThat(inner.contains("name"))
          .isFalse();
    }

    @Test
    public void getting_missing_binding_throws_exception() {
      inner = bindings(aMap());
      assertCall(() -> inner.get("name"))
          .throwsException(NoSuchElementException.class);
    }

    @Test
    public void contains_binding_that_is_in_inner_scope() {
      inner = bindings(aMap(elem("name", 7)));
      assertThat(inner.contains("name"))
          .isTrue();
    }

    @Test
    public void binding_from_inner_scope_can_be_retrieved() {
      inner = bindings(aMap(elem("name", 7)));
      assertThat(inner.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
      outer = bindings(aMap(elem("name", 7)));
      inner = outer.newInnerScope(aMap());
      assertThat(inner.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void binding_in_current_scope_hides_binding_from_outer_scope() {
      outer = bindings(aMap(elem("name", 3)));
      inner = outer.newInnerScope(aMap(elem("name", 7)));
      assertThat(inner.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void to_string() {
      outer = bindings(aMap(elem("value-a", 7), elem("value-b", 8)));
      inner = outer.newInnerScope(aMap(elem("value-c", 9), elem("value-d", 10)));
      assertThat(inner.toString())
          .isEqualTo("""
              Elem[name=value-a, value=7]
              Elem[name=value-b, value=8]
                Elem[name=value-c, value=9]
                Elem[name=value-d, value=10]""");
    }
  }

  @Nested
  class _mutable_bindings {
    @Test
    public void empty_name_binding_doesnt_contain_any_binding() {
      mutable = bindings(aMap()).newInnerScope();
      assertThat(mutable.contains("name"))
          .isFalse();
    }

    @Test
    public void getting_missing_binding_throws_exception() {
      mutable = bindings(aMap()).newInnerScope();
      assertCall(() -> mutable.get("name"))
          .throwsException(NoSuchElementException.class);
    }

    @Test
    public void contains_binding_that_is_in_inner_scope() {
      mutable = bindings(aMap()).newInnerScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.contains("name"))
          .isTrue();
    }

    @Test
    public void binding_from_inner_scope_can_be_retrieved() {
      mutable = bindings(aMap()).newInnerScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
      mutable = bindings(aMap(elem("name", 7))).newInnerScope();
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void binding_in_current_scope_hides_binding_from_outer_scope() {
      mutable = bindings(aMap(elem("name", 3))).newInnerScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void to_string() {
      outer = bindings(aMap(elem("value-a", 7), elem("value-b", 8)));
      mutable = outer.newInnerScope();
      mutable.add(elem("value-c", 9));
      assertThat(mutable.toString())
          .isEqualTo("""
              Elem[name=value-a, value=7]
              Elem[name=value-b, value=8]
                Elem[name=value-c, value=9]""");
    }
  }

  private static Elem elem(String name, int value) {
    return new Elem(name, value);
  }

  public static ImmutableMap<String, Elem> aMap(Elem... nameables) {
    return Nameables.toMap(Arrays.asList(nameables));
  }
  private static record Elem(String name, Integer value) implements Named {}
}
