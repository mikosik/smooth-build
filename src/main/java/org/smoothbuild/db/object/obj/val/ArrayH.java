package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
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
  public ArrayTypeH type() {
    return (ArrayTypeH) super.type();
  }

  public <T extends ValueH> ImmutableList<T> elems(Class<T> elemJType) {
    assertIsIterableAs(elemJType);
    var elems = elemObjs();
    return checkTypeOfSequenceObjs(elems, type().elem());
  }

  private ImmutableList<ValueH> elemObjs() {
    return readSequenceObjs(DATA_PATH, dataHash(), ValueH.class);
  }

  private <T extends ValueH> void assertIsIterableAs(Class<T> clazz) {
    TypeH elem = this.type().elem();
    if (!(elem.isNothing() || clazz.isAssignableFrom(elem.jType()))) {
      throw new IllegalArgumentException(this.type().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkTypeOfSequenceObjs(
      ImmutableList<ValueH> elems, TypeHV expectedElementType) {
    for (int i = 0; i < elems.size(); i++) {
      var elemType = elems.get(i).type();
      if (!(objectDb().typing().isAssignable(expectedElementType, elemType))) {
        throw new UnexpectedObjNodeException(hash(), this.type(), DATA_PATH, i,
            expectedElementType, elemType);
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  @Override
  public String valueToString() {
    return "[" + sequenceToString(elemObjs()) + ']';
  }
}
