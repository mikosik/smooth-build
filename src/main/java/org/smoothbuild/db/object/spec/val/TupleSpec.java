package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.TUPLE;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleSpec extends ValSpec {
  private final ImmutableList<ValSpec> itemSpecs;

  public TupleSpec(Hash hash, Iterable<? extends ValSpec> itemSpecs) {
    super(calculateName(itemSpecs), hash, TUPLE);
    this.itemSpecs = ImmutableList.copyOf(itemSpecs);
  }

  @Override
  public Tuple newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Tuple(merkleRoot, objectDb);
  }

  public ImmutableList<ValSpec> items() {
    return itemSpecs;
  }

  private static String calculateName(Iterable<? extends ValSpec> itemSpecs) {
    return "{" + toCommaSeparatedString(itemSpecs, Spec::name) + "}";
  }
}
