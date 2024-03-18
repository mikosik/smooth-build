Bucket implementation is wrapped by SynchronizedBucket and
is @Singleton so all method calls will run in isolation.
Code that uses Bucket and does check-then-act
(by calling two different methods on Bucket)
should be written in such a way that either
it doesn't care when other thread changes something or be sure it won't happen.

