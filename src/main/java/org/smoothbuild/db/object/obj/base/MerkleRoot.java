package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class MerkleRoot {
  private final Hash hash;
  private final TypeH type;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, TypeH type, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.type = type;
  }

  public Hash hash() {
    return hash;
  }

  public TypeH type() {
    return type;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
