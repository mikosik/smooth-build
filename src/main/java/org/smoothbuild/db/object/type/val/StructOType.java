package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.util.collect.NamedList;

public class StructOType extends TypeV implements StructType {
  private final NamedList<TypeV> fields;

  public StructOType(Hash hash, String name, NamedList<TypeV> fields) {
    super(name, hash, STRUCT, calculateVariables(fields));
    this.fields = fields;
  }

  @Override
  public NamedList<TypeV> fields() {
    return fields;
  }

  @Override
  public Struc_ newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Struc_(merkleRoot, objDb);
  }
}
