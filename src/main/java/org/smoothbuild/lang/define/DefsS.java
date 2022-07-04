package org.smoothbuild.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.HashMap;

import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public record DefsS(
    ImmutableBindings<TDefS> tDefs,
    ImmutableBindings<TopRefableS> topRefables) {

  public static DefsS empty() {
    return new DefsS(immutableBindings(), immutableBindings());
  }

  public DefsS withModule(ModS mod) {
    return new DefsS(
        merge(tDefs, mod.tDefs()),
        merge(topRefables, mod.topRefables())
    );
  }

  public <E extends Named> ImmutableBindings<E> merge(
      ImmutableBindings<E> outer, ImmutableBindings<? extends E> inner) {
    var map = new HashMap<String, E>();
    map.putAll(outer.asMap());
    map.putAll(inner.asMap());
    return immutableBindings(ImmutableMap.copyOf(map));
  }
}
