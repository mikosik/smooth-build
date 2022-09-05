package org.smoothbuild.lang.define;

/**
 * Smooth value.
 */
public sealed interface ValS extends ExprS permits BlobS, FuncS, IntS, StringS {
}
