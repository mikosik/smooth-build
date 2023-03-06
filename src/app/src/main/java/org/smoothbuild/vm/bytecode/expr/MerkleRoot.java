package org.smoothbuild.vm.bytecode.expr;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class MerkleRoot {
  private final Hash hash;
  private final CategoryB category;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, CategoryB category, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.category = category;
  }

  public Hash hash() {
    return hash;
  }

  public CategoryB category() {
    return category;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
