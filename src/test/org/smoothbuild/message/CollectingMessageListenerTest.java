package org.smoothbuild.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.mockito.InOrder;

public class CollectingMessageListenerTest {
  Error error = new Error("error");
  Warning warning = new Warning("warning");
  Info info = new Info("warning");

  CollectingMessageListener collectingMessageListener = new CollectingMessageListener();

  @Test
  public void allCollectedMessagesAreReportedInOrder() {
    // given
    MessageListener listener = mock(MessageListener.class);

    // when
    collectingMessageListener.report(error);
    collectingMessageListener.report(warning);
    collectingMessageListener.reportCollectedMessagesTo(listener);

    // then
    InOrder inOrder = inOrder(listener);
    inOrder.verify(listener).report(error);
    inOrder.verify(listener).report(warning);
    verifyNoMoreInteractions(listener);
  }

  @Test
  public void isErrorReportedReturnsFalseWhenNothingReported() throws Exception {
    assertThat(collectingMessageListener.isErrorReported()).isFalse();
  }

  @Test
  public void isErrorReportedReturnsTrueWhenOneIsReported() throws Exception {
    collectingMessageListener.report(error);
    assertThat(collectingMessageListener.isErrorReported()).isTrue();
  }

  @Test
  public void isErrorReportedReturnsFalseWhenWarningAndInfoWasReported() throws Exception {
    collectingMessageListener.report(info);
    collectingMessageListener.report(warning);
    assertThat(collectingMessageListener.isErrorReported()).isFalse();
  }
}
