package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Nameables;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public class BindingsTest {
  private ImmutableBindings<Elem> immutable;
  private MutableBindings<Elem> mutable;

  @Nested
  class _immutable_bindings {
    @Test
    public void empty_name_binding_doesnt_contain_any_binding() {
      immutable = immutableBindings(aMap());
      assertThat(immutable.contains("name"))
          .isFalse();
    }

    @Test
    public void getting_missing_binding_throws_exception() {
      immutable = immutableBindings(aMap());
      assertCall(() -> immutable.get("name"))
          .throwsException(NoSuchElementException.class);
    }

    @Test
    public void getOpt_missing_binding_returns_empty() {
      immutable = immutableBindings(aMap());
      assertThat(immutable.getOpt("name"))
          .isEqualTo(Optional.empty());
    }

    @Test
    public void contains_binding_that_was_added_during_construction() {
      immutable = immutableBindings(aMap(elem("name", 7)));
      assertThat(immutable.contains("name"))
          .isTrue();
    }

    @Test
    public void binding_can_be_retrieved() {
      immutable = immutableBindings(aMap(elem("name", 7)));
      assertThat(immutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void binding_can_be_retrieved_via_getOpt() {
      immutable = immutableBindings(aMap(elem("name", 7)));
      assertThat(immutable.getOpt("name"))
          .isEqualTo(Optional.of(elem("name", 7)));
    }

    @Test
    public void to_string() {
      immutable = immutableBindings(aMap(elem("value-a", 7), elem("value-b", 8)));
      assertThat(immutable.toString())
          .isEqualTo("""
              Elem[name=value-a, value=7]
              Elem[name=value-b, value=8]""");
    }
  }

  @Nested
  class _mutable_bindings {
    @Test
    public void empty_name_binding_doesnt_contain_any_binding() {
      mutable = immutableBindings(aMap()).newMutableScope();
      assertThat(mutable.contains("name"))
          .isFalse();
    }

    @Test
    public void getting_missing_binding_throws_exception() {
      mutable = immutableBindings(aMap()).newMutableScope();
      assertCall(() -> mutable.get("name"))
          .throwsException(NoSuchElementException.class);
    }

    @Test
    public void getOpt_missing_binding_return_empty() {
      mutable = immutableBindings(aMap()).newMutableScope();
      assertThat(mutable.getOpt("name"))
          .isEqualTo(Optional.empty());
    }

    @Test
    public void contains_binding_that_is_in_inner_scope() {
      mutable = immutableBindings(aMap()).newMutableScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.contains("name"))
          .isTrue();
    }

    @Test
    public void binding_from_inner_scope_can_be_retrieved() {
      mutable = immutableBindings(aMap()).newMutableScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void when_no_binding_in_current_scope_then_binding_from_outer_scope_is_returned() {
      mutable = immutableBindings(aMap(elem("name", 7))).newMutableScope();
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void binding_in_current_scope_hides_binding_from_outer_scope() {
      mutable = immutableBindings(aMap(elem("name", 3))).newMutableScope();
      mutable.add(elem("name", 7));
      assertThat(mutable.get("name"))
          .isEqualTo(elem("name", 7));
    }

    @Test
    public void to_string() {
      immutable = immutableBindings(aMap(elem("value-a", 7), elem("value-b", 8)));
      mutable = immutable.newMutableScope()
          .add(elem("value-c", 9));
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
