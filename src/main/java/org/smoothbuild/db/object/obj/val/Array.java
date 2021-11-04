package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.val.ArrayTypeO;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Array extends Val {
  public Array(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public ArrayTypeO type() {
    return (ArrayTypeO) super.type();
  }

  public <T extends Val> ImmutableList<T> elements(Class<T> elementJType) {
    assertIsIterableAs(elementJType);
    var elements = elementObjs();
    return checkTypeOfSequenceObjs(elements, this.type().element());
  }

  private ImmutableList<Obj> elementObjs() {
    return readSequenceObjs(DATA_PATH, dataHash());
  }

  private <T extends Val> void assertIsIterableAs(Class<T> clazz) {
    TypeO element = this.type().element();
    if (!(element.isNothing() || clazz.isAssignableFrom(element.jType()))) {
      throw new IllegalArgumentException(this.type().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkTypeOfSequenceObjs(
      ImmutableList<Obj> elements, TypeO type) {
    for (int i = 0; i < elements.size(); i++) {
      TypeO elementType = elements.get(i).type();
      if (!(type.equals(elementType))) {
        throw new UnexpectedObjNodeException(hash(), this.type(), DATA_PATH, i, type, elementType);
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elements;
    return result;
  }

  @Override
  public String valueToString() {
    return "[" + sequenceToString(elementObjs()) + ']';
  }
}
