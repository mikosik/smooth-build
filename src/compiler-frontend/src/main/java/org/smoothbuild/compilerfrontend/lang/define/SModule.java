package org.smoothbuild.compilerfrontend.lang.define;

/**
 * Smooth module containing type, function and value definitions.
 * @param localScope scope with module members
 * @param fullScope scope with module and imported module members
 */
public record SModule(SScope localScope, SScope fullScope) {}
