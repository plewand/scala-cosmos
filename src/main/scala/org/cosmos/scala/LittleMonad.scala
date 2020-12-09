package org.cosmos.scala

import scala.concurrent.{ExecutionContext, Future}

type Id[A] = A

// To provide a type abstraction for the program flow. See also the unit tests for usage.
trait LittleMonad[F[_]] {
  def pure[A](value: A): F[A]

  def flatMap[A, B](m: F[A])(f: A => F[B]): F[B]

  def map[A, B](m: F[A])(f: A => B): F[B] = flatMap(m)(v => pure(f(v)))
}

// Extension methods for the type that LittleMonad instance is defined for.
object LittleMonadExtensions {

  extension[B, F[_], A] (v: A)(using monad: LittleMonad[F]) {
    def pure: F[A] = monad.pure(v)
  }

  extension[B, F[_], A] (m: F[A])(using monad: LittleMonad[F]) {
    def flatMap(f: A => F[B]): F[B] = monad.flatMap(m)(f)

    def map(f: A => B): F[B] = monad.map(m)(f)
  }
}

// Monad instances are defined in one place for clearance. Future is used during the regular program flow,
// Id during the tests.
object LittleMonadInstances {

  given (using ExecutionContext) as LittleMonad[Future] {
    def pure[A](value: A): Future[A] = Future.successful(value)

    def flatMap[A, B](m: Future[A])(f: A => Future[B]): Future[B] = m.flatMap(f)
  }

  given LittleMonad[Id] {
    def pure[A](value: A): Id[A] = value

    def flatMap[A, B](m: Id[A])(f: A => Id[B]): Id[B] = f(m)
  }

}

