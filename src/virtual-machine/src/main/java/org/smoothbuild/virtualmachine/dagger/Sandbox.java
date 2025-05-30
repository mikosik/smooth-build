package org.smoothbuild.virtualmachine.dagger;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;

/**
 * Hash of sandbox in which execution of tasks takes place.
 */
@Qualifier
@Retention(RUNTIME)
public @interface Sandbox {}
