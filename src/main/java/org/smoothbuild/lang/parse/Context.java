package org.smoothbuild.lang.parse;

import java.util.Optional;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Referencable;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.CallableNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Context {
  private final Definitions imported;
  private final ImmutableMap<String, CallableNode> callables;

  public Context(Definitions imported, ImmutableMap<String, CallableNode> callables) {
    this.imported = imported;
    this.callables = callables;
  }

  public ImmutableList<ItemSignature> parametersOf(String name) {
    Referencable referencable = imported.referencables().get(name);
    if (referencable != null) {
      return ((Callable) referencable).type().parameters();
    }
    CallableNode node = callables.get(name);
    if (node != null) {
      return node.parameterSignatures();
    }
    throw new RuntimeException("Couldn't find `" + name + "` function.");
  }

  public Optional<Type> resultTypeOf(String name) {
    Callable callable = (Callable) imported.referencables().get(name);
    if (callable != null) {
      return Optional.of(callable.resultType());
    }
    CallableNode node = callables.get(name);
    if (node != null) {
      return node.resultType();
    }
    throw new RuntimeException("Couldn't find `" + name + "` function.");
  }
}
