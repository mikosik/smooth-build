package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;

import com.google.common.collect.Lists;

public class ArrayBuilder<T extends Value> {
  private final ArrayMarshaller<T> marshaller;
  private final Class<?> elementClass;
  private final List<T> result;

  public ArrayBuilder(ArrayMarshaller<T> arrayMarshaller, Class<?> elementClass) {
    this.marshaller = arrayMarshaller;
    this.elementClass = elementClass;
    this.result = Lists.newArrayList();
  }

  public ArrayBuilder<T> add(T elem) {
    checkNotNull(elem);
    if (!elementClass.isAssignableFrom(elem.getClass())) {
      throw new IllegalArgumentException("Element must be of type "
          + elementClass.getCanonicalName());
    }
    result.add(elem);
    return this;
  }

  public Array<T> build() {
    return marshaller.write(result);
  }
}
