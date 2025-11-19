package org.smoothbuild.virtualmachine.dagger;

import jakarta.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Dagger scope of Virtual machine instance.
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PerVm {}
