package org.smoothbuild.filesystem.install;

import static org.smoothbuild.common.collect.Lists.map;

import org.smoothbuild.vm.bytecode.hashed.Hash;

import io.vavr.collection.Array;

public record HashNode(String name, Hash hash, Array<HashNode> children) {
  public HashNode(String name, Hash hash) {
    this(name, hash, Array.of());
  }

  public HashNode(String name, Array<HashNode> children) {
    this(name, hashOfChildren(children), children);
  }

  private static Hash hashOfChildren(Array<HashNode> children) {
    return Hash.of(map(children, HashNode::hash));
  }

  public String toPrettyString() {
    return name + " " + hash();
  }
}
