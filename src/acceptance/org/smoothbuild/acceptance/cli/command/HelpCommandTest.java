package org.smoothbuild.acceptance.cli.command;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class HelpCommandTest extends AcceptanceTestCase {
  @Test
  public void help_command_prints_general_help() {
    runSmoothHelp();
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth COMMAND
        
        Description:
        smooth-build is a build tool with strongly and statically typed, purely
        functional language. It features fine-grained, aggressive caching that will
        make sure no computation happens twice on the same machine, decreasing build
        times significantly.
        More info at https://github.com/mikosik/smooth-build/blob/master/doc/tutorial.md
        
        Options:
        
        Commands:
          build    Build artifact(s) by running specified function(s)
          clean    Remove all cached objects and artifacts calculated during all
                     previous builds
          help     Displays help information about the specified command
          list     Print arg-less user defined functions
          tree     Print execution tree for specified function(s)
          version  Print version information
        """);
  }

  @Test
  public void help_build() {
    runSmoothHelp("build");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth build [-d=<projectDir>] [-l=<level>] [-s=<filter>] <function>...
                   
        Description:
        Build artifact(s) by running specified function(s)
                   
        Parameters:
              <function>...         function(s) which results are saved as artifacts
                   
        Options:
          -d, --project-dir=<projectDir>
                                    Project directory where 'build.smooth' is located.
                                      By default equal to current directory.
                   
          -l, --log-level=<level>   Show logs with specified level or above.
                   
                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs
                   
          -s, --show-tasks=<filter> Show executed build tasks that match filter.
                   
                                    Filter is a boolean expression made up of matchers
                                      (listed below), boolean operators '&', '|',
                                      grouping brackets '(', ')'.
                                    Default value is '(user&call)|info'
                   
                                    For each matched tasks its name and properties are
                                      printed together with logs that match filter
                                      specified with --log-level option. Note that you
                                      can filter tasks by one log level and its logs by
                                      other level. For example setting
                                      '--show-tasks=error --log-level=warning' prints
                                      tasks that have a log with at least error level
                                      and for each such a task all logs with at least
                                      warning level.
                   
                                    Available task matchers:
                                      all              - all tasks
                                      default          - shortcut for '(user&call)|info'
                                      none             - no tasks
                   
                                      f, fatal         - contains a log with fatal level
                                      e, error         - contains a log with at least
                                      error level
                                      w, warning       - contains a log with at least
                                      warning level
                                      i, info          - contains any log
                   
                                      u, user          - evaluates expression from user
                                      module
                                      s, slib          - evaluates expression from
                                      smooth standard library module
                   
                                      c, call          - evaluates function call
                                      conv, conversion - evaluates automatic conversion
                                      l, literal       - evaluates compile time literal
           """);
  }

  @Test
  public void help_clean() {
    runSmoothHelp("clean");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth clean [-d=<projectDir>] [-l=<level>] 
                   
        Description:
        Remove all cached objects and artifacts calculated during all previous builds 
                   
        Options:
          -d, --project-dir=<projectDir>
                                    Project directory where 'build.smooth' is located.
                                      By default equal to current directory.
                   
          -l, --log-level=<level>   Show logs with specified level or above.
                   
                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

        """);
  }

  @Test
  public void help_list() {
    runSmoothHelp("list");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth list [-d=<projectDir>] [-l=<level>]
                
        Description:
        Print arg-less user defined functions
                
        Options:
          -d, --project-dir=<projectDir>
                                    Project directory where 'build.smooth' is located.
                                      By default equal to current directory.
                
          -l, --log-level=<level>   Show logs with specified level or above.
                
                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

        """);
  }

  @Test
  public void help_tree() {
    runSmoothHelp("tree");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth tree [-d=<projectDir>] [-l=<level>] <function>...
                
        Description:
        Print execution tree for specified function(s)
                
        Parameters:
              <function>...         function(s) which execution tree is printed
                
        Options:
          -d, --project-dir=<projectDir>
                                    Project directory where 'build.smooth' is located.
                                      By default equal to current directory.
                
          -l, --log-level=<level>   Show logs with specified level or above.
                
                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs
        
        """);
  }

  @Test
  public void help_version() {
    runSmoothHelp("version");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Usage:
        smooth version [-l=<level>]
                
        Description:
        Print version information
                
        Options:
          -l, --log-level=<level>   Show logs with specified level or above.
                
                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs
        
        """);
  }
}
