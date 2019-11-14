package org.smoothbuild.lang.object.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;

public class MerkleRoot {
  private final Hash hash;
  private final ConcreteType type;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, ConcreteType type, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.type = type;
  }

  public Hash hash() {
    return hash;
  }

  public ConcreteType type() {
    return type;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
