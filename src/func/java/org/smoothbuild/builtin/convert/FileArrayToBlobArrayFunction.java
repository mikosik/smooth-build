package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class FileArrayToBlobArrayFunction {
  @SmoothFunction
  public static Array<Blob> fileArrayToBlobArray( //
      Container container, //
      @Required @Name("input") Array<SFile> input) {
    ArrayBuilder<Blob> builder = container.arrayBuilder(Blob.class);
    for (SFile file : input) {
      builder.add(file.content());
    }
    return builder.build();
  }
}
