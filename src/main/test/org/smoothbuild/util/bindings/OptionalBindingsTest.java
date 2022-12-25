package org.smoothbuild.util.bindings;

import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OptionalBindingsTest {
  @Test
  public void calling_inner_scope_bindings_fails_when_some_binding_is_empty() {
    var bindings = new OptionalBindings<>(immutableBindings());
    bindings.add("name", Optional.empty());
    assertCall(bindings::innerScopeBindingsReduced)
        .throwsException(new NoSuchElementException("Nothing bound for name `name`."));
  }
}
