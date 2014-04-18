package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.Lists;

public class ArrayBuilderImpl<T extends SValue> implements ArrayBuilder<T> {
  private final SArrayType<T> arrayType;
  private final ArrayMarshaller<T> marshaller;
  private final List<T> result;

  public ArrayBuilderImpl(HashedDb hashedDb, SArrayType<T> arrayType,
      ArrayMarshaller<T> arrayMarshaller) {
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
  public SArray<T> build() {
    return marshaller.write(result);
  }
}
