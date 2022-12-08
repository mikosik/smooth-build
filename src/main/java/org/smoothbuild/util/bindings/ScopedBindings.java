package org.smoothbuild.util.bindings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Splitter;

public class ScopedBindings<E> extends AbstractBindings<E> {
  private final Bindings<? extends E> outerScopeBindings;
  protected final Map<String, E> innerScopeBindings;

  public ScopedBindings(Bindings<? extends E> outerScopeBindings) {
    this.outerScopeBindings = outerScopeBindings;
    this.innerScopeBindings = new HashMap<>();
  }

  public void add(String name, E element) {
    innerScopeBindings.put(checkNotNull(name), checkNotNull(element));
  }

  @Override
  public E getOrNull(String name) {
    E element = innerScopeBindings.get(name);
    if (element == null) {
      return outerScopeBindings.getOrNull(name);
    } else {
      return element;
    }
  }

  @Override
  public Map<String, E> asMap() {
    var result = new HashMap<String, E>();
    result.putAll(outerScopeBindings.asMap());
    result.putAll(innerScopeBindings);
    return result;
  }

  @Override
  public String toString() {
    return bindingsToString(innerScopeBindings)
        + "\n"
        + indent(outerScopeBindings.toString());

  }

  private String indent(String text) {
    var lines = Splitter.onPattern("\r?\n").split(text);
    return stream(lines)
        .map(l -> "  " + l)
        .collect(joining("\n"));
  }
}
