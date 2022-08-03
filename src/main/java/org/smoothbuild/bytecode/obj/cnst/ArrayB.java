package org.smoothbuild.bytecode.obj.cnst;

import static com.google.common.base.Suppliers.memoize;

import java.util.function.Supplier;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends CnstB {
  private final Supplier<ImmutableList<CnstB>> elemsSupplier;

  public ArrayB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
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

  public <T extends CnstB> ImmutableList<T> elems(Class<T> elemTJ) {
    assertIsIterableAs(elemTJ);
    return (ImmutableList<T>) elemsSupplier.get();
  }

  private <T extends CnstB> void assertIsIterableAs(Class<T> clazz) {
    CatB elem = this.cat().elem();
    if (!clazz.isAssignableFrom(elem.typeJ())) {
      throw new IllegalArgumentException(this.cat().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<CnstB> instantiateElems() {
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

  private ImmutableList<CnstB> readElemObjs() {
    return readSeqObjs(DATA_PATH, dataHash(), CnstB.class);
  }

  @Override
  public String objToString() {
    return "[" + objsToString(readElemObjs()) + ']';
  }
}
