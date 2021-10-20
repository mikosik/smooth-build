package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.ArraySpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Array extends Val {
  public Array(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public ArraySpec spec() {
    return (ArraySpec) super.spec();
  }

  public <T extends Val> ImmutableList<T> elements(Class<T> elementJType) {
    assertIsIterableAs(elementJType);
    var elements = elementObjs();
    return checkSpecOfSequenceObjs(elements, spec().element());
  }

  private ImmutableList<Obj> elementObjs() {
    return readSequenceObjs(DATA_PATH, dataHash());
  }

  private <T extends Val> void assertIsIterableAs(Class<T> clazz) {
    Spec element = spec().element();
    if (!(element.isNothing() || clazz.isAssignableFrom(element.jType()))) {
      throw new IllegalArgumentException(spec().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  protected <T> ImmutableList<T> checkSpecOfSequenceObjs(ImmutableList<Obj> elements, Spec spec) {
    for (int i = 0; i < elements.size(); i++) {
      Spec elementSpec = elements.get(i).spec();
      if (!(spec.equals(elementSpec))) {
        throw new UnexpectedObjNodeException(hash(), spec(), DATA_PATH, i, spec, elementSpec);
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
