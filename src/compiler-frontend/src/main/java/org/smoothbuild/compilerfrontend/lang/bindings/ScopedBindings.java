package org.smoothbuild.compilerfrontend.lang.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.collect.Map;

public sealed class ScopedBindings<E> implements Bindings<E>
    permits ImmutableScopedBindings, MutableScopedBindings {
  private final Bindings<? extends E> outerScopeBindings;
  private final Bindings<? extends E> innerScopeBindings;

  protected ScopedBindings(
      Bindings<? extends E> outerScopeBindings, Bindings<? extends E> innerScopeBindings) {
    this.outerScopeBindings = requireNonNull(outerScopeBindings);
    this.innerScopeBindings = requireNonNull(innerScopeBindings);
  }

  @Override
  public E get(String name) {
    E element = innerScopeBindings.get(name);
    return element != null ? element : outerScopeBindings.get(name);
  }

  @Override
  public Map<String, E> toMap() {
    @SuppressWarnings("unchecked")
    Map<String, E> map = (Map<String, E>) outerScopeBindings.toMap();
    return map.overrideWith(innerScopeBindings.toMap());
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
