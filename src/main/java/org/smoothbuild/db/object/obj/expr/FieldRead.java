package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Int;

/**
 * This class is immutable.
 */
public class FieldRead extends Expr {
  private static final int DATA_HASH_LIST_SIZE = 2;
  private static final int TUPLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public FieldRead(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Expr tuple() {
    Obj tuple = getDataSequenceElementObj(TUPLE_INDEX, DATA_HASH_LIST_SIZE);
    if (tuple instanceof Expr expr) {
      return expr;
    } else {
      throw new DecodeObjException(
          hash(), "Its data[0] should contain Expr but contains Val.");
    }
  }

  public Int index() {
    Obj index = getDataSequenceElementObj(INDEX_INDEX, DATA_HASH_LIST_SIZE);
    if (index instanceof Int intVal) {
      return intVal;
    } else {
      throw new DecodeObjException(
          hash(), "Its data[1] should contain INT but contains STRING.");
    }
  }

  @Override
  public String valueToString() {
    return "FieldRead(???)";
  }
}
