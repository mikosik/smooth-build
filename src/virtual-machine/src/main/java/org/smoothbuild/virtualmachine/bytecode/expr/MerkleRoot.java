package org.smoothbuild.virtualmachine.bytecode.expr;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;

public class MerkleRoot {
  private final Hash hash;
  private final BCategory category;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, BCategory category, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.category = category;
  }

  public Hash hash() {
    return hash;
  }

  public BCategory category() {
    return category;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
