package org.smoothbuild.common.dagger;

import jakarta.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Dagger scope of single command execution.
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PerCommand {}
