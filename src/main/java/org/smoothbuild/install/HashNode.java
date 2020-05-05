package org.smoothbuild.install;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class HashNode {
  private final String name;
  private final Hash hash;
  private final ImmutableList<HashNode> children;

  public HashNode(String name, Hash hash) {
    this(name, hash, ImmutableList.of());
  }

  public HashNode(String name, ImmutableList<HashNode> children) {
    this(name, hashOfChildren(children), children);
  }

  private static Hash hashOfChildren(ImmutableList<HashNode> children) {
    return Hash.of(children.stream().map(c -> c.hash).toArray(Hash[]::new));
  }

  private HashNode(String name, Hash hash, ImmutableList<HashNode> children) {
    this.name = name;
    this.hash = hash;
    this.children = children;
  }

  public String name() {
    return name;
  }

  public Hash hash() {
    return hash;
  }

  public ImmutableList<HashNode> children() {
    return children;
  }
}
