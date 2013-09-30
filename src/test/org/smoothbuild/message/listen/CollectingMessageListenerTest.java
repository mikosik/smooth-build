package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.mockito.InOrder;
import org.smoothbuild.message.message.Message;

public class CollectingMessageListenerTest {
  Message errorMessage = new Message(ERROR, "error");
  Message warningMessage = new Message(WARNING, "warning");
  Message infoMessage = new Message(INFO, "warning");

  CollectingMessageListener collectingMessageListener = new CollectingMessageListener();

  @Test
  public void allCollectedMessagesAreReportedInOrder() {
    // given
    MessageListener listener = mock(MessageListener.class);

    // when
    collectingMessageListener.report(errorMessage);
    collectingMessageListener.report(warningMessage);
    collectingMessageListener.reportCollectedMessagesTo(listener);

    // then
    InOrder inOrder = inOrder(listener);
    inOrder.verify(listener).report(errorMessage);
    inOrder.verify(listener).report(warningMessage);
    verifyNoMoreInteractions(listener);
  }

  @Test
  public void isErrorReportedReturnsFalseWhenNothingReported() throws Exception {
    assertThat(collectingMessageListener.isErrorReported()).isFalse();
  }

  @Test
  public void isErrorReportedReturnsTrueWhenOneIsReported() throws Exception {
    collectingMessageListener.report(errorMessage);
    assertThat(collectingMessageListener.isErrorReported()).isTrue();
  }

  @Test
  public void isErrorReportedReturnsFalseWhenWarningAndInfoWasReported() throws Exception {
    collectingMessageListener.report(infoMessage);
    collectingMessageListener.report(warningMessage);
    assertThat(collectingMessageListener.isErrorReported()).isFalse();
  }
}
