package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Value;

import com.google.common.collect.Lists;

public class ArrayBuilderImpl<T extends Value> implements ArrayBuilder<T> {
  private final ArrayMarshaller<T> marshaller;
  private final Class<?> elementClass;
  private final List<T> result;

  public ArrayBuilderImpl(ArrayMarshaller<T> arrayMarshaller, Class<?> elementClass) {
    this.marshaller = arrayMarshaller;
    this.elementClass = elementClass;
    this.result = Lists.newArrayList();
  }

  @Override
  public ArrayBuilder<T> add(T elem) {
    checkNotNull(elem);
    if (!elementClass.isAssignableFrom(elem.getClass())) {
      throw new IllegalArgumentException("Element must be of type "
          + elementClass.getCanonicalName());
    }
    result.add(elem);
    return this;
  }

  @Override
  public Array<T> build() {
    return marshaller.write(result);
  }
}
