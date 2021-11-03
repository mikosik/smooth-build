package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.util.collect.NamedList;

public class StructOType extends ValType implements StructType {
  private final NamedList<ValType> fields;

  public StructOType(Hash hash, String name, NamedList<ValType> fields) {
    super(name, hash, STRUCT, calculateVariables(fields));
    this.fields = fields;
  }

  @Override
  public NamedList<ValType> fields() {
    return fields;
  }

  @Override
  public Struc_ newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Struc_(merkleRoot, objectDb);
  }
}
