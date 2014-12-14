package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;

import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.base.Predicate;

public class FilterFunction {

  public interface FilterParameters {
    @Required
    public Array<SFile> files();

    @Required
    public SString include();
  }

  @SmoothFunction
  public static Array<SFile> filter(NativeApi nativeApi, FilterParameters params) {
    Predicate<Path> filter = createFilter(params.include().value());
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(SFile.class);

    for (SFile file : params.files()) {
      if (filter.apply(file.path())) {
        builder.add(file);
      }
    }

    return builder.build();
  }

  private static Predicate<Path> createFilter(String pattern) {
    try {
      return pathMatcher(pattern);
    } catch (IllegalPathPatternException e) {
      throw new IllegalPathPatternError("include", e.getMessage());
    }
  }
}
