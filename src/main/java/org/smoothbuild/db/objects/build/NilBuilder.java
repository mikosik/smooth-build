package org.smoothbuild.db.objects.build;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.SNothing;

import com.google.common.collect.ImmutableList;

public class NilBuilder implements ArrayBuilder<SNothing> {
  private final ArrayMarshaller<SNothing> marshaller;

  public NilBuilder(ArrayMarshaller<SNothing> marshaller) {
    this.marshaller = marshaller;
  }

  @Override
  public ArrayBuilder<SNothing> add(SNothing element) {
    throw new UnsupportedOperationException("Cannot add element to Nil");
  }

  @Override
  public Array<SNothing> build() {
    return marshaller.write(ImmutableList.<SNothing> of());
  }
}
