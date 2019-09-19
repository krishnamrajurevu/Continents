package model

import play.api.libs.json.Json

case class Continent(continentId: Int, continentName: String)

case class ContinentRef(continentName: String)

object Continent{
  implicit val continentFormat = Json.format[Continent]
}

object ContinentRef{
  implicit val continentFormat = Json.format[ContinentRef]
}