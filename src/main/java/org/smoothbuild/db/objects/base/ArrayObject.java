package org.smoothbuild.db.objects.base;

import java.util.Iterator;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Value;

import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;

public class ArrayObject<T extends Value> extends AbstractObject implements Array<T> {
  private final ArrayMarshaller<T> marshaller;

  public ArrayObject(HashCode hash, ArrayType<T> arrayType, ArrayMarshaller<T> marshaller) {
    super(arrayType, hash);
    this.marshaller = marshaller;
  }

  @Override
  public Iterator<T> iterator() {
    return marshaller.readElements(hash()).iterator();
  }

  @Override
  public String toString() {
    return "[" + Joiner.on(", ").join(this) + "]";
  }
}
