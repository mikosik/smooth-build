package org.smoothbuild.testing.problem;

import javax.inject.Singleton;

import org.smoothbuild.problem.ProblemsListener;

import com.google.inject.AbstractModule;

public class TestingProblemsListenerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestingProblemsListener.class).in(Singleton.class);
    bind(ProblemsListener.class).to(TestingProblemsListener.class);
  }
}
