package org.smoothbuild.bytecode.obj.val;

import static com.google.common.base.Suppliers.memoize;

import java.util.function.Supplier;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.val.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends ValB {
  private final Supplier<Object> elemsSupplier;

  public ArrayB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    super(merkleRoot, byteDb);
    this.elemsSupplier = memoize(this::instantiateElems);
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
    return (ImmutableList<T>) elemsSupplier.get();
  }

  private <T extends ValB> void assertIsIterableAs(Class<T> clazz) {
    CatB elem = this.cat().elem();
    if (!(elem.isNothing() || clazz.isAssignableFrom(elem.typeJ()))) {
      throw new IllegalArgumentException(this.cat().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<ValB> instantiateElems() {
    var elems = readElemObjs();
    var expectedElemT = type().elem();
    for (int i = 0; i < elems.size(); i++) {
      var elemT = elems.get(i).cat();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeObjWrongNodeTypeExc(hash(), cat(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elems;
  }

  private ImmutableList<ValB> readElemObjs() {
    return readSeqObjs(DATA_PATH, dataHash(), ValB.class);
  }

  @Override
  public String objToString() {
    return "[" + objsToString(readElemObjs()) + ']';
  }
}
