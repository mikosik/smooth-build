package org.smoothbuild.lang.parse;

import static java.util.Objects.requireNonNullElseGet;

import java.util.Map;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.like.CallableLike;
import org.smoothbuild.lang.base.like.ItemLike;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Referencables {
  private final ImmutableMap<String, Referencable> imported;
  private final Map<String, ? extends ReferencableLike> local;

  public Referencables(ImmutableMap<String, Referencable> imported,
      Map<String, ? extends ReferencableLike> local) {
    this.imported = imported;
    this.local = local;
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
    return requireNonNullElseGet(imported.get(name), () -> local.get(name));
  }
}
