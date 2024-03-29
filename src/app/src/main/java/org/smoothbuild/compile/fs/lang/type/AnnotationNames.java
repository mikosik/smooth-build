package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.util.collect.Sets.set;

import com.google.common.collect.ImmutableSet;

public class AnnotationNames {
  public static final String NATIVE_PURE = "Native";
  public static final String NATIVE_IMPURE = "NativeImpure";
  public static final String BYTECODE = "Bytecode";
  public static final ImmutableSet<String> ANNOTATION_NAMES = set(
      BYTECODE, NATIVE_PURE, NATIVE_IMPURE);
}
