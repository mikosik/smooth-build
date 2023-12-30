package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;

import org.smoothbuild.common.collect.Set;

public class AnnotationNames {
  public static final String NATIVE_PURE = "Native";
  public static final String NATIVE_IMPURE = "NativeImpure";
  public static final String BYTECODE = "Bytecode";
  public static final Set<String> ANNOTATION_NAMES = set(BYTECODE, NATIVE_PURE, NATIVE_IMPURE);
}
