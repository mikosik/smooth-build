package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.bindings.OptionalBindings.newOptionalBindings;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class OptionalBindingsTest {
  @Nested
  class _optional_bindings_wrapping_immutable_bindings {
    @Test
    public void get_returns_empty_bound_for_not_added_binding() {
      var bindings = newOptionalBindings(immutableBindings());
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>());
    }

    @Test
    public void get_returns_bound_value_when_binding_has_been_added() {
      var bindings = newOptionalBindings(immutableBindings());
      var value = Optional.of(7);
      bindings.add("name", value);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(value));
    }

    @Test
    public void get_returns_bound_empty_value_when_empty_binding_has_been_added() {
      var bindings = newOptionalBindings(immutableBindings());
      var value = Optional.empty();
      bindings.add("name", value);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(value));
    }

    @Test
    public void get_returns_bound_value_when_binding_has_been_added_to_wrapped_bindings() {
      var bindings = newOptionalBindings(immutableBindings(ImmutableMap.of("name", 7)));
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.of(7)));
    }

    @Test
    public void get_returns_bound_value_shadowing_binding_in_wrapped_bindings() {
      var bindings = newOptionalBindings(immutableBindings(ImmutableMap.of("name", 7)));
      bindings.add("name", Optional.of(3));
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.of(3)));
    }

    @Test
    public void get_returns_bound_empty_value_shadowing_binding_in_wrapped_bindings() {
      var bindings = newOptionalBindings(immutableBindings(ImmutableMap.of("name", 7)));
      bindings.add("name", Optional.empty());
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.empty()));
    }

    @Test
    public void inner_scope_bindings_contains_only_binding_from_inner_scope() {
      var bindings = newOptionalBindings(immutableBindings(ImmutableMap.of("name", 7)));
      bindings.add("name", Optional.of(3));
      assertThat(bindings.innerScopeBindings())
          .isEqualTo(immutableBindings(ImmutableMap.of("name", 3)));
    }
  }

  @Nested
  class _optional_bindings_wrapping_optional_bindings {
    @Test
    public void get_returns_empty_bound_for_not_added_binding() {
      var wrapped = newOptionalBindings(immutableBindings());
      var bindings = newOptionalBindings(wrapped);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>());
    }

    @Test
    public void get_returns_bound_value_when_binding_has_been_added() {
      var wrapped = newOptionalBindings(immutableBindings());
      var bindings = newOptionalBindings(wrapped);
      var value = Optional.of(7);
      bindings.add("name", value);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(value));
    }

    @Test
    public void get_returns_bound_empty_value_when_empty_binding_has_been_added() {
      var wrapped = newOptionalBindings(immutableBindings());
      var bindings = newOptionalBindings(wrapped);
      var value = Optional.empty();
      bindings.add("name", value);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(value));
    }

    @Test
    public void get_returns_bound_value_when_binding_has_been_added_to_wrapped_bindings() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.of(7));
      var bindings = newOptionalBindings(wrapped);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.of(7)));
    }

    @Test
    public void get_returns_bound_empty_value_when_binding_has_been_added_to_wrapped_bindings() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.empty());
      var bindings = newOptionalBindings(wrapped);
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.empty()));
    }

    @Test
    public void get_returns_bound_value_shadowing_binding_in_wrapped_bindings() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.of(7));
      var bindings = newOptionalBindings(wrapped);
      bindings.add("name", Optional.of(3));
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.of(3)));
    }

    @Test
    public void get_returns_bound_value_shadowing_empty_binding_in_wrapped_bindings() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.empty());
      var bindings = newOptionalBindings(wrapped);
      bindings.add("name", Optional.of(3));
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.of(3)));
    }

    @Test
    public void get_returns_bound_empty_value_shadowing_binding_in_wrapped_bindings() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.of(7));
      var bindings = newOptionalBindings(wrapped);
      bindings.add("name", Optional.empty());
      assertThat(bindings.get("name"))
          .isEqualTo(new Bound<>(Optional.empty()));
    }

    @Test
    public void inner_scope_bindings_contains_only_binding_from_inner_scope() {
      var wrapped = newOptionalBindings(immutableBindings());
      wrapped.add("name", Optional.of(7));
      var bindings = newOptionalBindings(wrapped);
      bindings.add("name", Optional.of(3));
      assertThat(bindings.innerScopeBindings())
          .isEqualTo(immutableBindings(ImmutableMap.of("name", 3)));
    }
  }
}
