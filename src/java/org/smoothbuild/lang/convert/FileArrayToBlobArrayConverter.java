package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SValueBuilders;

public class FileArrayToBlobArrayConverter extends Converter<SArray<SFile>, SArray<SBlob>> {

  public FileArrayToBlobArrayConverter() {
    super(FILE_ARRAY, BLOB_ARRAY);
  }

  @Override
  public SArray<SBlob> convert(SValueBuilders valueBuilders, SArray<SFile> value) {
    checkArgument(value.type() == FILE_ARRAY);

    ArrayBuilder<SBlob> blobArrayBuilder = valueBuilders.arrayBuilder(BLOB_ARRAY);
    for (SFile file : value) {
      blobArrayBuilder.add(file.content());
    }
    return blobArrayBuilder.build();
  }
}
