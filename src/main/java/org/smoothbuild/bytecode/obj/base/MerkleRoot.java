package org.smoothbuild.bytecode.obj.base;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.db.Hash;

public class MerkleRoot {
  private final Hash hash;
  private final CatB cat;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, CatB cat, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.cat = cat;
  }

  public Hash hash() {
    return hash;
  }

  public CatB cat() {
    return cat;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
