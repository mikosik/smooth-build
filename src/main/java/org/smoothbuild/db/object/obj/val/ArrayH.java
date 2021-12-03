package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeExc;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class ArrayH extends ValH {
  public ArrayH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public ArrayTH cat() {
    return (ArrayTH) super.cat();
  }

  public <T extends ValH> ImmutableList<T> elems(Class<T> elemJType) {
    assertIsIterableAs(elemJType);
    var elems = elemObjs();
    return checkTypeOfSeqObjs(elems, this.cat().elem());
  }

  private ImmutableList<ValH> elemObjs() {
    return readSeqObjs(DATA_PATH, dataHash(), ValH.class);
  }

  private <T extends ValH> void assertIsIterableAs(Class<T> clazz) {
    CatH elem = this.cat().elem();
    if (!(elem.isNothing() || clazz.isAssignableFrom(elem.typeJ()))) {
      throw new IllegalArgumentException(this.cat().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkTypeOfSeqObjs(
      ImmutableList<ValH> elems, TypeH expectedElementType) {
    for (int i = 0; i < elems.size(); i++) {
      var elemType = elems.get(i).cat();
      if (!(objDb().typing().isAssignable(expectedElementType, elemType))) {
        throw new UnexpectedObjNodeExc(hash(), this.cat(), DATA_PATH, i,
            expectedElementType, elemType);
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  @Override
  public String objToString() {
    return "[" + seqToString(elemObjs()) + ']';
  }
}
