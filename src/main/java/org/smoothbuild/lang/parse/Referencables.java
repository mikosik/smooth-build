package org.smoothbuild.lang.parse;

import static java.util.Objects.requireNonNullElseGet;

import java.util.Map;

import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.like.ReferencableLike;

import com.google.common.collect.ImmutableMap;

public class Referencables {
  private final ImmutableMap<String, Referencable> imported;
  private final Map<String, Referencable> local;

  public Referencables(ImmutableMap<String, Referencable> imported,
      Map<String, Referencable> local) {
    this.imported = imported;
    this.local = local;
  }

  public ReferencableLike findReferencableLike(String name) {
    return requireNonNullElseGet(imported.get(name), () -> local.get(name));
  }
}
