package org.smoothbuild.install;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

public record HashNode(String name, Hash hash, ImmutableList<HashNode> children) {
  public HashNode(String name, Hash hash) {
    this(name, hash, list());
  }

  public HashNode(String name, ImmutableList<HashNode> children) {
    this(name, hashOfChildren(children), children);
  }

  private static Hash hashOfChildren(ImmutableList<HashNode> children) {
    return Hash.of(map(children, HashNode::hash));
  }
}
