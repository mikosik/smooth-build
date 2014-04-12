package org.smoothbuild.io.cache.value.build;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;

public class SValueBuildersImpl implements SValueBuilders {
  private final ValueDb valueDb;

  @Inject
  public SValueBuildersImpl(ValueDb valueDb) {
    this.valueDb = valueDb;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return valueDb.arrayBuilder(arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(valueDb);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(valueDb);
  }

  @Override
  public SString string(String string) {
    return valueDb.writeString(string);
  }
}
