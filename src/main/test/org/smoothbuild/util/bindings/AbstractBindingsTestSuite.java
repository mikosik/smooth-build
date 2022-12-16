package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Nameables;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractBindingsTestSuite {
  private Bindings<Elem> bindings;

  @Test
  public void getting_element() {
    bindings = newBindings(elem("name", 7));
    assertThat(bindings.get("name"))
        .isEqualTo(elem("name", 7));
  }

  @Test
  public void getting_missing_element_throws_exception() {
    bindings = newBindings();
    assertCall(() -> bindings.get("name"))
        .throwsException(new NoSuchElementException("name"));
  }

  @Test
  public void getOptional_element() {
    bindings = newBindings(elem("name", 7));
    assertThat(bindings.getOptional("name"))
        .isEqualTo(Optional.of(elem("name", 7)));
  }

  @Test
  public void getOrNull_missing_element_returns_null() {
    bindings = newBindings();
    assertThat(bindings.getOptional("name"))
        .isEqualTo(Optional.empty());
  }

  @Test
  public void contains_present_element() {
    bindings = newBindings(elem("name", 7));
    assertThat(bindings.contains("name"))
        .isTrue();
  }

  @Test
  public void contains_missing_element_returns_false() {
    bindings = newBindings();
    assertThat(bindings.contains("name"))
        .isFalse();
  }

  @Test
  public void map() {
    bindings = newBindings(elem("name", 7), elem("other", 5));
    var mapped = bindings.map(elem -> elem.value);
    assertThat(mapped.get("name"))
        .isEqualTo(7);
  }

  @Test
  public void asMap() {
    bindings = newBindings(elem("name", 7), elem("other", 5));
    assertThat(bindings.asMap())
        .isEqualTo(mapOfElems(elem("name", 7), elem("other", 5)));
  }

  public abstract Bindings<Elem> newBindings(Elem... elems);

  public static ImmutableMap<String, Elem> mapOfElems(Elem... nameables) {
    return Nameables.toMap(Arrays.asList(nameables));
  }

  public static Elem elem(String name, int value) {
    return new Elem(name, value);
  }

  protected static record Elem(String name, Integer value) implements Named {}
}
