package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;

public class MerkleRoot {
  private final Hash hash;
  private final CatH cat;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, CatH cat, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.cat = cat;
  }

  public Hash hash() {
    return hash;
  }

  public CatH cat() {
    return cat;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
