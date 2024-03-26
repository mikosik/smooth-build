package org.smoothbuild.virtualmachine.bytecode.expr;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.base.BKind;

public class MerkleRoot {
  private final Hash hash;
  private final BKind kind;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, BKind kind, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.kind = kind;
  }

  public Hash hash() {
    return hash;
  }

  public BKind kind() {
    return kind;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
