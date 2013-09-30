package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.junit.Test;
import org.mockito.InOrder;
import org.smoothbuild.message.message.InfoMessage;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WarningMessage;

public class CollectingMessageListenerTest {
  Message errorMessage = new Message(ERROR, "error");
  WarningMessage warningMessage = new WarningMessage("warning");
  InfoMessage infoMessage = new InfoMessage("warning");

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
