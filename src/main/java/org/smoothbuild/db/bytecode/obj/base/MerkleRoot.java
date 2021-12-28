package org.smoothbuild.db.bytecode.obj.base;

import org.smoothbuild.db.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;

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
