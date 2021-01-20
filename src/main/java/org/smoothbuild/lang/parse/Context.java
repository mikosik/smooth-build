package org.smoothbuild.lang.parse;

import java.util.Optional;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Context {
  private final Definitions imported;
  private final ImmutableMap<String, ReferencableNode> referencables;

  public Context(Definitions imported, ImmutableMap<String, ReferencableNode> referencables) {
    this.imported = imported;
    this.referencables = referencables;
  }

  public ImmutableList<ItemSignature> parametersOf(String name) {
    if (imported.referencables().get(name) instanceof Callable callable) {
      return callable.type().parameters();
    }
    if (referencables.get(name) instanceof CallableNode callableNode) {
      return callableNode.parameterSignatures();
    }
    throw new RuntimeException("Couldn't find `" + name + "` function.");
  }

  public Optional<Type> resultTypeOf(String name) {
    if (imported.referencables().get(name) instanceof Callable callable) {
      return Optional.of(callable.resultType());
    }
    if (referencables.get(name) instanceof CallableNode callableNode) {
      return callableNode.resultType();
    }
    throw new RuntimeException("Couldn't find `" + name + "` function.");
  }
}
