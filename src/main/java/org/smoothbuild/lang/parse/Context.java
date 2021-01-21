package org.smoothbuild.lang.parse;

import static java.util.Objects.requireNonNullElseGet;

import java.util.Map;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.like.CallableLike;
import org.smoothbuild.lang.base.like.ItemLike;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public class Context {
  private final Definitions imported;
  private final Map<String, ? extends ReferencableLike> referencables;

  public Context(Definitions imported, Map<String, ? extends ReferencableLike> referencables) {
    this.imported = imported;
    this.referencables = referencables;
  }

  public ImmutableList<ItemSignature> parametersOf(String name) {
    return findCallableLike(name).parameterSignatures();
  }

  public ImmutableList<? extends ItemLike> parameterLikesOf(String name) {
    return findCallableLike(name).parameterLikes();
  }

  public Optional<Type> resultTypeOf(String name) {
    return findCallableLike(name).inferredResultType();
  }

  private CallableLike findCallableLike(String name) {
    return (CallableLike) findReferencableLike(name);
  }

  public ReferencableLike findReferencableLike(String name) {
    return requireNonNullElseGet(
        imported.referencables().get(name),
        () -> referencables.get(name));
  }
}
