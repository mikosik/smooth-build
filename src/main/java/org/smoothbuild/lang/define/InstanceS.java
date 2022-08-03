package org.smoothbuild.lang.define;

public sealed interface InstanceS extends ExprS permits BlobS, FuncS, IntS, StringS {
}
