package org.smoothbuild.compile.fs.lang.type.tool;

import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Constraint stating that two types are equal.
 */
public record EqualityConstraint(TypeS type1, TypeS type2) implements Constraint {}
