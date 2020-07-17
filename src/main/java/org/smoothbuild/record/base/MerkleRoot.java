package org.smoothbuild.record.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.spec.Spec;

public class MerkleRoot {
  private final Hash hash;
  private final Spec spec;
  private final Hash dataHash;

  public MerkleRoot(Hash hash, Spec spec, Hash dataHash) {
    this.hash = hash;
    this.dataHash = dataHash;
    this.spec = spec;
  }

  public Hash hash() {
    return hash;
  }

  public Spec spec() {
    return spec;
  }

  public Hash dataHash() {
    return dataHash;
  }
}
