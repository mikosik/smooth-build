package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Value;

import com.google.common.collect.Lists;

public class ArrayBuilderImpl<T extends Value> implements ArrayBuilder<T> {
  private final ArrayType<T> arrayType;
  private final ArrayMarshaller<T> marshaller;
  private final List<T> result;

  public ArrayBuilderImpl(ArrayType<T> arrayType, ArrayMarshaller<T> arrayMarshaller) {
    this.arrayType = arrayType;
    this.marshaller = arrayMarshaller;
    this.result = Lists.newArrayList();
  }

  @Override
  public ArrayBuilder<T> add(T elem) {
    checkNotNull(elem);
    checkArgument(elem.type().equals(arrayType.elemType()));
    result.add(elem);
    return this;
  }

  @Override
  public Array<T> build() {
    return marshaller.write(result);
  }
}
