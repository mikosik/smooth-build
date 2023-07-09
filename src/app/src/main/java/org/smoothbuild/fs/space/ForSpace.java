package org.smoothbuild.fs.space;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import jakarta.inject.Qualifier;

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ForSpace {
  Space value();
}
