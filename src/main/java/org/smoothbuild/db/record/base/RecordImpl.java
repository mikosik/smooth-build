package org.smoothbuild.db.record.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.spec.Spec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class RecordImpl implements Record {
  private final MerkleRoot merkleRoot;
  protected final HashedDb hashedDb;

  public RecordImpl(MerkleRoot merkleRoot, HashedDb hashedDb) {
    this.merkleRoot = merkleRoot;
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public Hash hash() {
    return merkleRoot.hash();
  }

  @Override
  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  @Override
  public Spec spec() {
    return merkleRoot.spec();
  }

  @Override
  public String valueToString() {
    return "...";
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof Record that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return valueToString() + ":" + hash();
  }

  public static String elementsToStringValues(ImmutableList<Record> records) {
    return records.stream().map(Record::valueToString).collect(joining(","));
  }
}
