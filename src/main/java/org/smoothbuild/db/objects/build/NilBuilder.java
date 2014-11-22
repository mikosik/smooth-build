package org.smoothbuild.db.objects.build;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Nothing;

import com.google.common.collect.ImmutableList;

public class NilBuilder implements ArrayBuilder<Nothing> {
  private final ArrayMarshaller<Nothing> marshaller;

  public NilBuilder(ArrayMarshaller<Nothing> marshaller) {
    this.marshaller = marshaller;
  }

  @Override
  public ArrayBuilder<Nothing> add(Nothing element) {
    throw new UnsupportedOperationException("Cannot add element to Nil");
  }

  @Override
  public Array<Nothing> build() {
    return marshaller.write(ImmutableList.<Nothing> of());
  }
}
