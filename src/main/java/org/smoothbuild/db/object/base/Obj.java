package org.smoothbuild.db.object.base;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;
import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodingObjectException;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.DecodingDataHashSequenceException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.Spec;

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
    return wrapObjectDbExceptionAsDecodingObjectException(
        hash(), () -> {
          Hash hash = getDataSequenceImpl(expectedSize).get(i);
          return objectDb().get(hash);
        });
  }

  protected List<Hash> getDataSequence(int expectedSize) {
    return wrapObjectDbExceptionAsDecodingObjectException(
        hash(),
        () -> getDataSequenceImpl(expectedSize));
  }

  protected List<Hash> getDataSequence() {
    return wrapObjectDbExceptionAsDecodingObjectException(
        hash(),
        this::getDataSequenceImpl);
  }

  private List<Hash> getDataSequenceImpl(int expectedSize) {
    List<Hash> data = getDataSequenceImpl();
    if (data.size() != expectedSize) {
      throw new DecodingDataHashSequenceException(dataHash(), expectedSize, data.size());
    }
    return data;
  }

  private List<Hash> getDataSequenceImpl() {
    return wrapDecodingObjectException(dataHash(), () -> objectDb().readSequence(dataHash()));
  }
}
