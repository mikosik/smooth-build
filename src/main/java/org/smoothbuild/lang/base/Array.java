package org.smoothbuild.lang.base;

import java.util.Iterator;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;

import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;

/**
 * Array Value in smooth language.
 */
public class Array<T extends Value> extends AbstractValue implements Iterable<T> {
  private final ArrayMarshaller<T> marshaller;

  public Array(HashCode hash, ArrayType arrayType, ArrayMarshaller<T> marshaller) {
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
