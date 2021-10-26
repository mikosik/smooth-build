package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class StructSpec extends ValSpec implements StructType {
  private final NamedList<ValSpec> fields;

  public StructSpec(Hash hash, String name, ImmutableList<Named<ValSpec>> fields) {
    super(name, hash, STRUCT, calculateVariables(fields));
    this.fields = namedList(fields);
  }

  @Override
  public NamedList<ValSpec> fields() {
    return fields;
  }

  @Override
  public Struc_ newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Struc_(merkleRoot, objectDb);
  }
}
