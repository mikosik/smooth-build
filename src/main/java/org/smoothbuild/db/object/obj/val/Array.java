package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
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
    ImmutableList<Obj> elements = elements();
    for (Obj object : elements) {
      if (!object.spec().equals(spec().element())) {
        throw new DecodeObjException(hash(), "It is array which spec == " + spec().name()
            + " but one of its elements has spec == " + object.spec().name());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) (Object) elements;
    return result;
  }

  private <T extends Val> void assertIsIterableAs(Class<T> clazz) {
    Spec element = spec().element();
    if (!(element.isNothing() || clazz.isAssignableFrom(element.jType()))) {
      throw new IllegalArgumentException(spec().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<Obj> elements() {
    List<Hash> elementsHashes = getDataSequence();
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(),
        () -> map(elementsHashes, h -> objectDb().get(h)));
  }

  @Override
  public String valueToString() {
    return "[" + elementsToStringValues(elements()) + ']';
  }
}
