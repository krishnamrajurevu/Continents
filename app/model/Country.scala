package model

import play.api.libs.json.Json

case class Country(countryId: Int, countryName: String, continentId: Int)

case class CountryRef(countryName: String, continentId: Int)

case class CountryResponse(message: String,status: String,countries: Seq[Country])

object Country{
  implicit val countryFormat= Json.format[Country]
}

object CountryRef{
  implicit val countryRefFormat= Json.format[CountryRef]
}

object CountryResponse{
  implicit val countryResponseFormat= Json.format[CountryResponse]
}


