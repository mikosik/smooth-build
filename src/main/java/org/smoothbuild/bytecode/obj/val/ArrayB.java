package org.smoothbuild.bytecode.obj.val;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeCatExc;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends ValB {
  public ArrayB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    super(merkleRoot, byteDb);
  }

  @Override
  public ArrayTB type() {
    return cat();
  }

  @Override
  public ArrayTB cat() {
    return (ArrayTB) super.cat();
  }

  public <T extends ValB> ImmutableList<T> elems(Class<T> elemTJ) {
    assertIsIterableAs(elemTJ);
    var elems = elemObjs();
    return checkTypeOfSeqObjs(elems, this.cat().elem());
  }

  private ImmutableList<ValB> elemObjs() {
    return readSeqObjs(ObjB.DATA_PATH, dataHash(), ValB.class);
  }

  private <T extends ValB> void assertIsIterableAs(Class<T> clazz) {
    CatB elem = this.cat().elem();
    if (!(elem.isNothing() || clazz.isAssignableFrom(elem.typeJ()))) {
      throw new IllegalArgumentException(this.cat().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private <T> ImmutableList<T> checkTypeOfSeqObjs(ImmutableList<ValB> elems, TypeB expectedElemT) {
    for (int i = 0; i < elems.size(); i++) {
      var elemT = elems.get(i).cat();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeObjWrongNodeCatExc(hash(), this.cat(), ObjB.DATA_PATH, i, expectedElemT, elemT);
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  @Override
  public String objToString() {
    return "[" + objsToString(elemObjs()) + ']';
  }
}
