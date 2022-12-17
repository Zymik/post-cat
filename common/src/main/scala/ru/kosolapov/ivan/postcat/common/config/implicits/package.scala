package ru.kosolapov.ivan.postcat.common.config

import com.comcast.ip4s.Port
import io.circe.{Decoder, DecodingFailure, HCursor}


package object implicits {
  implicit val PortDecoder: Decoder[Port] = (c: HCursor) => {
    c.as[Int].map(Port.fromInt).flatMap {
      case Some(value) => Right(value)
      case None => Left(DecodingFailure(s"Cannot convert value to Port", c.history))
    }
  }
}
