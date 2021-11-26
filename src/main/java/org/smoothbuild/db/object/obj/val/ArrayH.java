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

  public <T extends ValueH> ImmutableList<T> elements(Class<T> elementJType) {
    assertIsIterableAs(elementJType);
    var elements = elementObjs();
    return checkTypeOfSequenceObjs(elements, type().element());
  }

  private ImmutableList<ValueH> elementObjs() {
    return readSequenceObjs(DATA_PATH, dataHash(), ValueH.class);
  }

  private <T extends ValueH> void assertIsIterableAs(Class<T> clazz) {
    TypeH element = this.type().element();
    if (!(element.isNothing() || clazz.isAssignableFrom(element.jType()))) {
      throw new IllegalArgumentException(this.type().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkTypeOfSequenceObjs(
      ImmutableList<ValueH> elements, TypeHV expectedElementType) {
    for (int i = 0; i < elements.size(); i++) {
      var elementType = elements.get(i).type();
      if (!(objectDb().typing().isAssignable(expectedElementType, elementType))) {
        throw new UnexpectedObjNodeException(hash(), this.type(), DATA_PATH, i,
            expectedElementType, elementType);
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
