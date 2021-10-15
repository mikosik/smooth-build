package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.ValSpec;

import com.google.common.collect.ImmutableList;

public class StructSpec extends ValSpec {
  private final RecSpec items;
  private final ImmutableList<String> names;
  private final String name;

  public StructSpec(Hash hash, String name, RecSpec items, ImmutableList<String> names) {
    super(hash, STRUCT);
    this.name = name;
    this.items = items;
    this.names = names;
  }

  public RecSpec rec() {
    return items;
  }

  public ImmutableList<String> names() {
    return names;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Struc_ newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Struc_(merkleRoot, objectDb);
  }
}
