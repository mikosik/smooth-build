package org.smoothbuild.builtin;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.FilterFunction;
import org.smoothbuild.builtin.file.MergeFunction;
import org.smoothbuild.builtin.file.NewFileFunction;
import org.smoothbuild.builtin.file.SaveFunction;
import org.smoothbuild.builtin.java.JarFunction;
import org.smoothbuild.builtin.java.UnjarFunction;
import org.smoothbuild.builtin.java.javac.JavacFunction;
import org.smoothbuild.builtin.java.junit.JunitFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class BuiltinFunctions {
  public static final ImmutableList<Class<?>> BUILTIN_FUNCTION_CLASSES = createList();

  private static ImmutableList<Class<?>> createList() {
    Builder<Class<?>> builder = ImmutableList.builder();
    // file related
    builder.add(FileFunction.class);
    builder.add(FilesFunction.class);
    builder.add(NewFileFunction.class);
    builder.add(FilterFunction.class);
    builder.add(MergeFunction.class);
    builder.add(SaveFunction.class);

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
