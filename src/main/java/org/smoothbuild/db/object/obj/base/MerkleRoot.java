package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;

public class MerkleRoot {
  private final Hash hash;
  private final ObjType type;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, ObjType type, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.type = type;
  }

  public Hash hash() {
    return hash;
  }

  public ObjType type() {
    return type;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
