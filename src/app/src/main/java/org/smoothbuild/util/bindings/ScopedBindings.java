package org.smoothbuild.util.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Maps.override;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public sealed class ScopedBindings<E>
    extends AbstractBindings<E>
    permits ScopedImmutableBindings, ScopedMutableBindings {
  private final Bindings<? extends E> outerScopeBindings;
  private final Bindings<? extends E> innerScopeBindings;

  protected ScopedBindings(
      Bindings<? extends E> outerScopeBindings,
      Bindings<? extends E> innerScopeBindings) {
    this.outerScopeBindings = requireNonNull(outerScopeBindings);
    this.innerScopeBindings = requireNonNull(innerScopeBindings);
  }

  @Override
  public Optional<E> getOptional(String name) {
    Optional<E> element = innerScopeBindings.getOptional(name).map(e -> e);
    if (element.isPresent()) {
      return element;
    } else {
      return outerScopeBindings.getOptional(name).map(e -> e);
    }
  }

  @Override
  public <T> ScopedBindings<T> map(Function<? super E, T> mapper) {
    return new ScopedBindings<>(
        outerScopeBindings.map(mapper),
        innerScopeBindings.map(mapper)
    );
  }

  @Override
  public ImmutableMap<String, E> toMap() {
    return override(innerScopeBindings.toMap(), outerScopeBindings.toMap());
  }

  public Bindings<? extends E> outerScopeBindings() {
    return outerScopeBindings;
  }

  public Bindings<? extends E> innerScopeBindings() {
    return innerScopeBindings;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ScopedBindings<?> that
        && Objects.equals(this.outerScopeBindings, that.outerScopeBindings)
        && Objects.equals(this.innerScopeBindings, that.innerScopeBindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outerScopeBindings, innerScopeBindings);
  }

  @Override
  public String toString() {
    return outerScopeBindings + "\n" + indent(innerScopeBindings.toString());
  }
}
