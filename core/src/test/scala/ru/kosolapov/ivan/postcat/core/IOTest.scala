package ru.kosolapov.ivan.postcat.core

import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalamock.scalatest.{AsyncMockFactory, MockFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec

trait IOTest extends AsyncFreeSpec with AsyncIOSpec with Matchers with AsyncMockFactory {


}
