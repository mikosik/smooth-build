package org.smoothbuild.record.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.type.BinaryType;

public class MerkleRoot {
  private final Hash hash;
  private final BinaryType type;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, BinaryType type, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.type = type;
  }

  public Hash hash() {
    return hash;
  }

  public BinaryType type() {
    return type;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
