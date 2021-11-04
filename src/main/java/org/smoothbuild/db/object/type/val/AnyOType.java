package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.AnyType;

/**
 * This class is immutable.
 */
public class AnyOType extends ValType implements AnyType {
  public AnyOType(Hash hash) {
    super("Any", hash, ANY);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + ANY + " type.");
  }
}