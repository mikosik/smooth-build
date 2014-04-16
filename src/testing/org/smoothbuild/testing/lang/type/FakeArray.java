package org.smoothbuild.testing.lang.type;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.base.AbstractObject;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeArray<T extends SValue> extends AbstractObject implements SArray<T> {
  private final List<T> elements = Lists.newArrayList();

  @SuppressWarnings("unchecked")
  public static <T extends SValue> FakeArray<T> fakeArray(SType<SArray<T>> type, T... elements) {
    FakeArray<T> array = new FakeArray<T>(type);
    for (T elem : elements) {
      array.add(elem);
    }
    return array;
  }

  public FakeArray(SType<?> type) {
    super(type, HashCode.fromInt(0));
  }

  public void add(T file) {
    elements.add(file);
  }

  @Override
  public Iterator<T> iterator() {
    return elements.iterator();
  }

  @Override
  public HashCode hash() {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return Hash.bytes(marshaller.getBytes());
  }
}
