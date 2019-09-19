package model

import play.api.libs.json.Json

case class Country(countryId: Int, countryName: String, continentId: Int)

case class CountryRef(countryName: String, continentId: Int)

object Country{
  implicit val countryFormat= Json.format[Country]
}

object CountryRef{
  implicit val countryRefFormat= Json.format[CountryRef]
}


