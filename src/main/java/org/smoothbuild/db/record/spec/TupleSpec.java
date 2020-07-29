package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.db.RecordDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleSpec extends Spec {
  private final ImmutableList<Spec> elementSpecs;

  public TupleSpec(MerkleRoot merkleRoot, Iterable<? extends Spec> elementSpecs,
      HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, SpecKind.TUPLE, hashedDb, recordDb);
    this.elementSpecs = ImmutableList.copyOf(elementSpecs);
  }

  @Override
  public Tuple newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Tuple(merkleRoot, recordDb, hashedDb);
  }

  @Override
  public String name() {
    String elementNames = elementSpecs.stream().map(Spec::name).collect(joining(","));
    return "{" + elementNames + "}";
  }

  public ImmutableList<Spec> elementSpecs() {
    return elementSpecs;
  }
}
