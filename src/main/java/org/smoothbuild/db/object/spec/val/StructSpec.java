package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.fieldsMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.StructType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class StructSpec extends ValSpec implements StructType {
  private final ImmutableList<ValSpec> fields;
  private final ImmutableList<String> names;
  private final ImmutableMap<String, Integer> nameToIndex;

  public StructSpec(Hash hash, String name, ImmutableList<ValSpec> fields,
      ImmutableList<String> names) {
    super(name, hash, STRUCT, calculateVariables(fields));
    this.fields = fields;
    this.names = names;
    this.nameToIndex = fieldsMap(names);
  }

  @Override
  public ImmutableList<ValSpec> fields() {
    return fields;
  }

  @Override
  public ImmutableMap<String, Integer> nameToIndex() {
    return nameToIndex;
  }

  @Override
  public ImmutableList<String> names() {
    return names;
  }

  @Override
  public Struc_ newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Struc_(merkleRoot, objectDb);
  }
}
