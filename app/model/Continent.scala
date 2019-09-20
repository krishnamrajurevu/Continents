package model

import play.api.libs.json.Json

case class Continent(continentId: Int, continentName: String)

case class ContinentRef(continentName: String)

case class ContinentStrResponse(message: String,status: String)

case class ContinentResponse(message : String,status: String, continent: Seq[Continent])

object Continent{
  implicit val continentFormat = Json.format[Continent]
}

object ContinentRef{
  implicit val continentRefFormat = Json.format[ContinentRef]
}

object ContinentStrResponse{
  implicit val continentStrResFormat = Json.format[ContinentStrResponse]
}

object ContinentResponse{
  implicit val continentResFormat = Json.format[ContinentResponse]
}