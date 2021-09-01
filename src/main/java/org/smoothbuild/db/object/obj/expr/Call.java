package org.smoothbuild.db.object.obj.expr;

import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeObjException;
import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;
import static org.smoothbuild.util.Lists.map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Call extends Expr {
  private static final int DATA_HASH_LIST_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public Call(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Expr function() {
    Obj obj = getDataSequenceElementObj(FUNCTION_INDEX, DATA_HASH_LIST_SIZE);
    if (obj instanceof Expr expr) {
      return expr;
    } else {
      throw new DecodeObjException(hash(),
          "Its data[0] should contain Expr but contains " + obj.spec().name() + ".");
    }
  }

  public ImmutableList<Expr> arguments() {
    return verifyExprList(getArguments());
  }

  private ImmutableList<Obj> getArguments() {
    Hash argumentsHash = getDataSequence(DATA_HASH_LIST_SIZE).get(ARGUMENTS_INDEX);
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(), () -> wrapHashedDbExceptionAsDecodeObjException(
            argumentsHash, () -> {
              var elementHashes = objectDb().readSequence(argumentsHash);
              return map(elementHashes, h -> objectDb().get(h));
            })
    );
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
