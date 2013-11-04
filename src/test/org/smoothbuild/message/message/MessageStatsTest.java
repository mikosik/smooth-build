package org.smoothbuild.message.message;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.FATAL;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.SUGGESTION;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessageStatsTest {
  MessageStats messageStats;
  MessageStats messageStats2;

  // initially count is zero

  @Test
  public void initially_fatal_count_is_zero() {
    given(messageStats = new MessageStats());
    when(messageStats.getCount(FATAL));
    thenReturned(0);
  }

  @Test
  public void initially_error_count_is_zero() {
    given(messageStats = new MessageStats());
    when(messageStats.getCount(ERROR));
    thenReturned(0);
  }

  @Test
  public void initially_warning_count_is_zero() {
    given(messageStats = new MessageStats());
    when(messageStats.getCount(WARNING));
    thenReturned(0);
  }

  @Test
  public void initially_suggestion_count_is_zero() {
    given(messageStats = new MessageStats());
    when(messageStats.getCount(SUGGESTION));
    thenReturned(0);
  }

  @Test
  public void initially_info_count_is_zero() {
    given(messageStats = new MessageStats());
    when(messageStats.getCount(INFO));
    thenReturned(0);
  }

  // count is one after incrementing

  @Test
  public void fatal_count_is_one_after_incrementing_it() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(FATAL);
    when(messageStats.getCount(FATAL));
    thenReturned(1);
  }

  @Test
  public void error_count_is_one_after_incrementing_it() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(ERROR);
    when(messageStats.getCount(ERROR));
    thenReturned(1);
  }

  @Test
  public void warning_count_is_one_after_incrementing_it() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(WARNING);
    when(messageStats.getCount(WARNING));
    thenReturned(1);
  }

  @Test
  public void suggestion_count_is_one_after_incrementing_it() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(SUGGESTION);
    when(messageStats.getCount(SUGGESTION));
    thenReturned(1);
  }

  @Test
  public void info_count_is_one_after_incrementing_it() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(INFO);
    when(messageStats.getCount(INFO));
    thenReturned(1);
  }

  // containsProblems()

  @Test
  public void initially_contains_no_problems() throws Exception {
    given(messageStats = new MessageStats());
    when(messageStats.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_info() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(INFO);
    when(messageStats.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_suggestion() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(SUGGESTION);
    when(messageStats.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_warning() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(WARNING);
    when(messageStats.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_problem_after_adding_error() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(ERROR);
    when(messageStats.containsProblems());
    thenReturned(true);
  }

  @Test
  public void contains_problem_after_adding_fatal() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats).incCount(FATAL);
    when(messageStats.containsProblems());
    thenReturned(true);
  }

  // add()

  @Test
  public void fatals_are_added_when_message_stats_are_added() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats2 = new MessageStats());
    given(messageStats).incCount(FATAL);
    given(messageStats2).incCount(FATAL);

    when(messageStats).add(messageStats2);

    thenEqual(messageStats.getCount(FATAL), 2);
    thenEqual(messageStats.getCount(ERROR), 0);
    thenEqual(messageStats.getCount(WARNING), 0);
    thenEqual(messageStats.getCount(SUGGESTION), 0);
    thenEqual(messageStats.getCount(INFO), 0);
  }

  @Test
  public void errors_are_added_when_message_stats_are_added() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats2 = new MessageStats());
    given(messageStats).incCount(ERROR);
    given(messageStats2).incCount(ERROR);

    when(messageStats).add(messageStats2);

    thenEqual(messageStats.getCount(FATAL), 0);
    thenEqual(messageStats.getCount(ERROR), 2);
    thenEqual(messageStats.getCount(WARNING), 0);
    thenEqual(messageStats.getCount(SUGGESTION), 0);
    thenEqual(messageStats.getCount(INFO), 0);
  }

  @Test
  public void warnings_are_added_when_message_stats_are_added() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats2 = new MessageStats());
    given(messageStats).incCount(WARNING);
    given(messageStats2).incCount(WARNING);

    when(messageStats).add(messageStats2);

    thenEqual(messageStats.getCount(FATAL), 0);
    thenEqual(messageStats.getCount(ERROR), 0);
    thenEqual(messageStats.getCount(WARNING), 2);
    thenEqual(messageStats.getCount(SUGGESTION), 0);
    thenEqual(messageStats.getCount(INFO), 0);
  }

  @Test
  public void suggestions_are_added_when_message_stats_are_added() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats2 = new MessageStats());
    given(messageStats).incCount(SUGGESTION);
    given(messageStats2).incCount(SUGGESTION);

    when(messageStats).add(messageStats2);

    thenEqual(messageStats.getCount(FATAL), 0);
    thenEqual(messageStats.getCount(ERROR), 0);
    thenEqual(messageStats.getCount(WARNING), 0);
    thenEqual(messageStats.getCount(SUGGESTION), 2);
    thenEqual(messageStats.getCount(INFO), 0);
  }

  @Test
  public void infos_are_added_when_message_stats_are_added() throws Exception {
    given(messageStats = new MessageStats());
    given(messageStats2 = new MessageStats());
    given(messageStats).incCount(INFO);
    given(messageStats2).incCount(INFO);

    when(messageStats).add(messageStats2);

    thenEqual(messageStats.getCount(FATAL), 0);
    thenEqual(messageStats.getCount(ERROR), 0);
    thenEqual(messageStats.getCount(WARNING), 0);
    thenEqual(messageStats.getCount(SUGGESTION), 0);
    thenEqual(messageStats.getCount(INFO), 2);
  }
}
