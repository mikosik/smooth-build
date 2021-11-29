package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayH extends ValueH {
  public ArrayH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  @Override
  public ArrayTypeH spec() {
    return (ArrayTypeH) super.spec();
  }

  public <T extends ValueH> ImmutableList<T> elems(Class<T> elemJType) {
    assertIsIterableAs(elemJType);
    var elems = elemObjs();
    return checkTypeOfSeqObjs(elems, spec().elem());
  }

  private ImmutableList<ValueH> elemObjs() {
    return readSeqObjs(DATA_PATH, dataHash(), ValueH.class);
  }

  private <T extends ValueH> void assertIsIterableAs(Class<T> clazz) {
    SpecH elem = spec().elem();
    if (!(elem.isNothing() || clazz.isAssignableFrom(elem.typeJ()))) {
      throw new IllegalArgumentException(spec().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkTypeOfSeqObjs(
      ImmutableList<ValueH> elems, TypeH expectedElementType) {
    for (int i = 0; i < elems.size(); i++) {
      var elemType = elems.get(i).spec();
      if (!(objectDb().typing().isAssignable(expectedElementType, elemType))) {
        throw new UnexpectedObjNodeException(hash(), spec(), DATA_PATH, i,
            expectedElementType, elemType);
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  @Override
  public String valToString() {
    return "[" + seqToString(elemObjs()) + ']';
  }
}
