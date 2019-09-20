package model

import play.api.libs.json.Json

case class City(cityId: Int, cityName: String, countryId: Int)

case class CityRef(cityName: String, countryId: Int)

case class CityResponse(message: String,status: String, cities: Seq[City])

case class CityListResponse(message: String,status: String, cities: List[String])

object City{
  implicit val cityFormat = Json.format[City]
}

object CityRef{
  implicit val cityRefFormat = Json.format[CityRef]
}

object CityResponse{
  implicit val cityResponseFormat= Json.format[CityResponse]
}

object CityListResponse{
  implicit val cityListResponseFormat= Json.format[CityListResponse]
}

