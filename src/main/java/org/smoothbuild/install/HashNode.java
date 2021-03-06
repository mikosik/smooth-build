package org.smoothbuild.install;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.collect.ImmutableList;

public record HashNode(String name, Hash hash, ImmutableList<HashNode> children) {

  public HashNode(String name, Hash hash) {
    this(name, hash, ImmutableList.of());
  }

  public HashNode(String name, ImmutableList<HashNode> children) {
    this(name, hashOfChildren(children), children);
  }

  private static Hash hashOfChildren(ImmutableList<HashNode> children) {
    return Hash.of(children.stream().map(c -> c.hash).toArray(Hash[]::new));
  }
}
