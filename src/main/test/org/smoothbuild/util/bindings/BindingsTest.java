package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.bindings.Bindings.mutableBindings;
import static org.smoothbuild.util.collect.Labeled.labeled;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.collect.Labeled;

import com.google.common.testing.EqualsTester;

public class BindingsTest {
  @Test
  public void equals_and_hashcode() {
    var labeled1 = labeled("1", "one");
    var labeled2 = labeled("2", "two");

    var equalsTester = new EqualsTester();
    // no elements
    equalsTester.addEqualityGroup(
        immutableBindings(),
        immutableBindings(Map.of()),
        mutableBindings()
    );

    // element-1 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindingsWith(labeled1),
        mutableBindingsWith(labeled1)
    );

    // element-2 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindingsWith(labeled2),
        mutableBindingsWith(labeled2)
    );

    // element-1 in outer scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(labeled1), Map.of()),
        mutableBindings(immutableBindingsWith(labeled1))
    );

    // element-2 in outer scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(labeled2), Map.of()),
        mutableBindings(immutableBindingsWith(labeled2))
    );

    // element-1 in outer scope and element-2 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(labeled1), Map.of(labeled2.nameSane(), labeled2)),
        mutableBindingsWith(immutableBindingsWith(labeled1), labeled2)
    );

    equalsTester.testEquals();
  }

  private static ImmutableBindings<Labeled<String>> immutableBindingsWith(Labeled<String> labeled) {
    return immutableBindings(Map.of(labeled.nameSane(), labeled));
  }

  private static Bindings<Labeled<String>> mutableBindingsWith(Labeled<String> labeled) {
    return mutableBindingsWith(null, labeled);
  }

  private static Bindings<Labeled<String>> mutableBindingsWith(
      Bindings<Labeled<String>> outerScopeBindings, Labeled<String> labeled) {
    var mutableBindings = mutableBindings(outerScopeBindings);
    mutableBindings.add(labeled.nameSane(), labeled);
    return mutableBindings;
  }
}
