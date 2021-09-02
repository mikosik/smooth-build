package org.smoothbuild.db.object.obj.base;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.db.DecodeDataSequenceException;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.base.Spec;

import com.google.common.collect.ImmutableList;

public abstract class Obj {
  private final MerkleRoot merkleRoot;
  private final ObjectDb objectDb;

  public Obj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    this.merkleRoot = merkleRoot;
    this.objectDb = objectDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ObjectDb objectDb() {
    return objectDb;
  }

  protected HashedDb hashedDb() {
    return objectDb.hashedDb();
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

  protected Obj getDataSequenceElementObj(int i, int expectedSize) {
    List<Hash> dataSequence = getDataSequence(expectedSize);
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(), () -> objectDb().get(dataSequence.get(i)));
  }

  protected List<Hash> getDataSequence(int expectedSize) {
    List<Hash> data = getDataSequence();
    if (data.size() != expectedSize) {
      throw new DecodeDataSequenceException(hash(), dataHash(), expectedSize, data.size());
    }
    return data;
  }

  protected List<Hash> getDataSequence() {
    try {
      return objectDb().readSequence(dataHash());
    } catch (HashedDbException e) {
      throw new DecodeObjException(hash(), new DecodeObjException(dataHash()));
    }
  }
}
