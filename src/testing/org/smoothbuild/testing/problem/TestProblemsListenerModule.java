package org.smoothbuild.testing.problem;

import javax.inject.Singleton;

import org.smoothbuild.problem.ProblemsListener;

import com.google.inject.AbstractModule;

public class TestProblemsListenerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestProblemsListener.class).in(Singleton.class);
    bind(ProblemsListener.class).to(TestProblemsListener.class);
  }
}
