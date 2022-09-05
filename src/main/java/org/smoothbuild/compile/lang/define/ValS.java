package org.smoothbuild.compile.lang.define;

/**
 * Smooth value.
 */
public sealed interface ValS extends ExprS permits BlobS, IntS, MonoRefableS, StringS {
}
