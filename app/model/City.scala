package model

import play.api.libs.json.Json

case class City(cityId: Int, cityName: String, countryId: Int)

case class CityRef(cityName: String, countryId: Int)

object City{
  implicit val cityFormat = Json.format[City]
}

object CityRef{
  implicit val cityRefFormat = Json.format[CityRef]
}
