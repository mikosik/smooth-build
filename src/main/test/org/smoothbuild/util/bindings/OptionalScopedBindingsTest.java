package org.smoothbuild.util.bindings;

import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OptionalScopedBindingsTest {
  @Test
  public void calling_inner_scope_bindings_fails_when_some_binding_is_empty() {
    var bindings = new OptionalScopedBindings<>(immutableBindings());
    bindings.add("name", Optional.empty());
    assertCall(bindings::innerScopeBindings)
        .throwsException(new NoSuchElementException("Nothing bound for name `name`."));
  }
}
