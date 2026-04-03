import cats.effect.{Deferred, IO, IOApp}
import cats.implicits.*

// cats-effect 3.x: ContextShift removed — IO is auto-shifted via IORuntime.
// Deferred moved from cats.effect.concurrent to cats.effect.
// Use IOApp instead of manually calling unsafeRunSync.
object DeferredApp extends IOApp.Simple:

  def start(d: Deferred[IO, Int]): IO[Unit] =
    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).attempt.void
    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)).void,
      d.get.flatMap(n => IO(println(show"Result: $n")))
    ).parSequence.void

  def run: IO[Unit] =
    for
      d <- Deferred[IO, Int]
      _ <- start(d)
    yield ()
