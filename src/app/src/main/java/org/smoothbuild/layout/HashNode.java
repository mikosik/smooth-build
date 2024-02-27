package org.smoothbuild.layout;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public record HashNode(String name, Hash hash, List<HashNode> children) {
  public HashNode(String name, Hash hash) {
    this(name, hash, list());
  }

  public HashNode(String name, List<HashNode> children) {
    this(name, hashOfChildren(children), children);
  }

  private static Hash hashOfChildren(List<HashNode> children) {
    return Hash.of(children.map(HashNode::hash));
  }

  public String toPrettyString() {
    return name + " " + hash();
  }
}
