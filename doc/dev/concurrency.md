FileSystem implementation is wrapped by SynchronizedFileSystem and
is @Singleton so all method calls will run in isolation.
Code that uses FileSystem and does check-then-act
(by calling two different methods on FileSystem)
should be written in such a way that either
it doesn't care when other thread changes something or be sure it won't happen.

