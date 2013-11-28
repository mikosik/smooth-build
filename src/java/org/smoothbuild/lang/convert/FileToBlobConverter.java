package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.FILE;

import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.lang.type.SValueBuilders;

public class FileToBlobConverter extends Converter<SBlob> {

  public FileToBlobConverter() {
    super(FILE, BLOB);
  }

  @Override
  public SBlob convert(SValueBuilders valueBuilders, SValue value) {
    checkArgument(value.type() == FILE);
    return ((SFile) value).content();
  }
}
