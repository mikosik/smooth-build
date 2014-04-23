package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueFactory;

public class FileArrayToBlobArrayConverter extends Converter<SArray<SFile>, SArray<SBlob>> {

  public FileArrayToBlobArrayConverter() {
    super(FILE_ARRAY, BLOB_ARRAY);
  }

  @Override
  public SArray<SBlob> convert(SValueFactory valueFactory, SArray<SFile> value) {
    checkArgument(value.type() == FILE_ARRAY);

    ArrayBuilder<SBlob> blobArrayBuilder = valueFactory.arrayBuilder(BLOB_ARRAY);
    for (SFile file : value) {
      blobArrayBuilder.add(file.content());
    }
    return blobArrayBuilder.build();
  }
}
