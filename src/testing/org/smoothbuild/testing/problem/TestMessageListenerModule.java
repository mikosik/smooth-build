package org.smoothbuild.testing.problem;

import javax.inject.Singleton;

import org.smoothbuild.problem.MessageListener;

import com.google.inject.AbstractModule;

public class TestMessageListenerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestMessageListener.class).in(Singleton.class);
    bind(MessageListener.class).to(TestMessageListener.class);
  }
}
