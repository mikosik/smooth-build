package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Constraint stating that two types are equal.
 */
public record Constraint(SType type1, SType type2) {}
