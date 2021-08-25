package org.smoothbuild.db.object.base;

import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;

import com.google.common.collect.ImmutableList;

public abstract class Obj {
  protected final MerkleRoot merkleRoot;

  public Obj(MerkleRoot merkleRoot) {
    this.merkleRoot = merkleRoot;
  }

  public static String elementsToStringValues(ImmutableList<? extends Obj> objects) {
    return objects.stream().map(Obj::valueToString).collect(joining(","));
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public Spec spec() {
    return merkleRoot.spec();
  }

  public abstract String valueToString();

  @Override
  public boolean equals(Object object) {
    return (object instanceof Obj that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return valueToString() + ":" + hash();
  }
}
