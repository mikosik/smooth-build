package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.db.object.spec.SpecKind.TUPLE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.db.ObjectDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleSpec extends Spec {
  private final ImmutableList<Spec> elementSpecs;

  public TupleSpec(Hash hash, Iterable<? extends Spec> elementSpecs, HashedDb hashedDb,
      ObjectDb objectDb) {
    super(hash, TUPLE, hashedDb, objectDb);
    this.elementSpecs = ImmutableList.copyOf(elementSpecs);
  }

  @Override
  public Tuple newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Tuple(merkleRoot, objectDb, hashedDb);
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
