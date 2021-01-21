package org.smoothbuild.lang.base.like;

import java.util.Optional;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public interface CallableLike {
  public Optional<Type> inferredResultType();

  public ImmutableList<ItemSignature> parameterSignatures();

  public ImmutableList<? extends ItemLike> parameterLikes();
}
