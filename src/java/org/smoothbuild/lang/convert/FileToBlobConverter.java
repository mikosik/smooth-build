package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.FILE;

import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueBuilders;

public class FileToBlobConverter extends Converter<SFile, SBlob> {

  public FileToBlobConverter() {
    super(FILE, BLOB);
  }

  @Override
  public SBlob convert(SValueBuilders valueBuilders, SFile value) {
    checkArgument(value.type() == FILE);
    return value.content();
  }
}
