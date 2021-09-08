package ru.tinkoff.gatling.transactions

import java.{util => ju}
import java.util.{Timer, TimerTask, concurrent => juc}

import io.netty.channel.{Channel, ChannelFuture, ChannelPromise, EventLoop, EventLoopGroup}
import io.netty.util.concurrent.{EventExecutor, Future => NFuture, ProgressivePromise, Promise, ScheduledFuture}

class FakeEventLoop extends EventLoop {
  private var timerInitialized = false
  private lazy val timer = {
    timerInitialized = true
    new Timer(true)
  }

  override def inEventLoop(): Boolean = true
  override def schedule(command: Runnable, delay: Long, unit: juc.TimeUnit): ScheduledFuture[_] = {
    timer.schedule(
      new TimerTask {
        override def run(): Unit = { command.run() }
      },
      unit.toMillis(delay)
    )
    null
  }
  override def execute(command: Runnable): Unit = command.run()

  override def shutdownGracefully(): NFuture[_] = {
    if (timerInitialized) {
      timer.cancel()
    }
    null
  }

  override def parent(): EventLoopGroup                                           = throw new UnsupportedOperationException
  override def next(): EventLoop                                                  = throw new UnsupportedOperationException
  override def register(channel: Channel): ChannelFuture                          = throw new UnsupportedOperationException
  override def register(promise: ChannelPromise): ChannelFuture                   = throw new UnsupportedOperationException
  override def register(channel: Channel, promise: ChannelPromise): ChannelFuture = throw new UnsupportedOperationException
  override def inEventLoop(thread: Thread): Boolean                               = throw new UnsupportedOperationException
  override def newPromise[V](): Promise[V]                                        = throw new UnsupportedOperationException
  override def newProgressivePromise[V](): ProgressivePromise[V]                  = throw new UnsupportedOperationException
  override def newSucceededFuture[V](result: V): NFuture[V]                       = throw new UnsupportedOperationException
  override def newFailedFuture[V](cause: Throwable): NFuture[V]                   = throw new UnsupportedOperationException
  override def isShuttingDown: Boolean                                            = throw new UnsupportedOperationException
  override def shutdownGracefully(quietPeriod: Long, timeout: Long, unit: juc.TimeUnit): NFuture[_] =
    throw new UnsupportedOperationException
  override def terminationFuture(): NFuture[_]                  = throw new UnsupportedOperationException
  override def shutdown(): Unit                                 = throw new UnsupportedOperationException
  override def shutdownNow(): ju.List[Runnable]                 = throw new UnsupportedOperationException
  override def iterator(): ju.Iterator[EventExecutor]           = throw new UnsupportedOperationException
  override def submit(task: Runnable): NFuture[_]               = throw new UnsupportedOperationException
  override def submit[T](task: Runnable, result: T): NFuture[T] = throw new UnsupportedOperationException
  override def submit[T](task: juc.Callable[T]): NFuture[T]     = throw new UnsupportedOperationException
  override def schedule[V](callable: juc.Callable[V], delay: Long, unit: juc.TimeUnit): ScheduledFuture[V] =
    throw new UnsupportedOperationException
  override def scheduleAtFixedRate(command: Runnable,
                                   initialDelay: Long,
                                   period: Long,
                                   unit: juc.TimeUnit): ScheduledFuture[_] =
    throw new UnsupportedOperationException
  override def scheduleWithFixedDelay(command: Runnable,
                                      initialDelay: Long,
                                      delay: Long,
                                      unit: juc.TimeUnit): ScheduledFuture[_] =
    throw new UnsupportedOperationException
  override def isShutdown: Boolean                                          = false
  override def isTerminated: Boolean                                        = throw new UnsupportedOperationException
  override def awaitTermination(timeout: Long, unit: juc.TimeUnit): Boolean = throw new UnsupportedOperationException
  override def invokeAll[T](tasks: ju.Collection[_ <: juc.Callable[T]]): ju.List[juc.Future[T]] =
    throw new UnsupportedOperationException
  override def invokeAll[T](tasks: ju.Collection[_ <: juc.Callable[T]],
                            timeout: Long,
                            unit: juc.TimeUnit): ju.List[juc.Future[T]] =
    throw new UnsupportedOperationException
  override def invokeAny[T](tasks: ju.Collection[_ <: juc.Callable[T]]): T = throw new UnsupportedOperationException
  override def invokeAny[T](tasks: ju.Collection[_ <: juc.Callable[T]], timeout: Long, unit: juc.TimeUnit): T =
    throw new UnsupportedOperationException
}
