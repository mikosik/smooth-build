package org.smoothbuild.virtualmachine.bytecode.load;

import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;

/**
 * Specifies java method by providing method name, binary name of enclosing class and jar content as
 * BlobB.
 */
public record MethodSpec(BMethod method, String methodName) {}
