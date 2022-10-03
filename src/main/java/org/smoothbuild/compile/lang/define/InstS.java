package org.smoothbuild.compile.lang.define;

/**
 * Instance of a value.
 */
public sealed interface InstS extends ExprS permits BlobS, EvaluableS, IntS, StringS {
}
