package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Nameables;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public class ImmutableBindingsTest {
  private ImmutableBindings<Elem> immutable;

  @Nested
  class _immutable_bindings {
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

  private static Elem elem(String name, int value) {
    return new Elem(name, value);
  }

  public static ImmutableMap<String, Elem> aMap(Elem... nameables) {
    return Nameables.toMap(Arrays.asList(nameables));
  }

  private static record Elem(String name, Integer value) implements Named {}
}
