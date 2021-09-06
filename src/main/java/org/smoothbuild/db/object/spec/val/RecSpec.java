package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class RecSpec extends ValSpec {
  private final ImmutableList<Spec> elementSpecs;

  public RecSpec(Hash hash, Iterable<? extends Spec> elementSpecs, ObjectDb objectDb) {
    super(hash, RECORD, objectDb);
    this.elementSpecs = ImmutableList.copyOf(elementSpecs);
  }

  @Override
  public Rec newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Rec(merkleRoot, objectDb());
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