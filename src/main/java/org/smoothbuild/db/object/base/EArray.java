package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class EArray extends Expr {
  public EArray(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public ImmutableList<Expr> elements() {
    return verifyExprList(getElements());
  }

  private ImmutableList<Obj> getElements() {
    List<Hash> elementsHashes = getDataSequence();
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(),
        () -> map(elementsHashes, h -> objectDb().get(h)));
  }

  @Override
  public String valueToString() {
    return "EArray(???)";
  }
}
