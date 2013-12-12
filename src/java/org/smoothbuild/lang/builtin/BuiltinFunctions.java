package org.smoothbuild.lang.builtin;

import org.smoothbuild.lang.builtin.blob.ConcatenateBlobsFunction;
import org.smoothbuild.lang.builtin.blob.ToFileFunction;
import org.smoothbuild.lang.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.builtin.compress.ZipFunction;
import org.smoothbuild.lang.builtin.file.ConcatenateFilesFunction;
import org.smoothbuild.lang.builtin.file.ContentFunction;
import org.smoothbuild.lang.builtin.file.FileFunction;
import org.smoothbuild.lang.builtin.file.FilesFunction;
import org.smoothbuild.lang.builtin.file.FilterFunction;
import org.smoothbuild.lang.builtin.java.JarFunction;
import org.smoothbuild.lang.builtin.java.UnjarFunction;
import org.smoothbuild.lang.builtin.java.javac.JavacFunction;
import org.smoothbuild.lang.builtin.java.junit.JunitFunction;
import org.smoothbuild.lang.builtin.string.ToBlobFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class BuiltinFunctions {
  public static final ImmutableList<Class<?>> BUILTIN_FUNCTION_CLASSES = createList();

  private static ImmutableList<Class<?>> createList() {
    Builder<Class<?>> builder = ImmutableList.builder();

    // String related
    builder.add(ToBlobFunction.class);

    // Blob related
    builder.add(ConcatenateBlobsFunction.class);
    builder.add(ToFileFunction.class);

    // File related
    builder.add(FileFunction.class);
    builder.add(FilesFunction.class);
    builder.add(ContentFunction.class);
    builder.add(ConcatenateFilesFunction.class);
    builder.add(FilterFunction.class);

    // java related
    builder.add(JavacFunction.class);
    builder.add(JarFunction.class);
    builder.add(UnjarFunction.class);
    builder.add(JunitFunction.class);

    // compression related
    builder.add(ZipFunction.class);
    builder.add(UnzipFunction.class);

    return builder.build();
  }
}
