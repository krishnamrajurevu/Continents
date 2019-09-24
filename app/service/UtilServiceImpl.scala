package service

import dao.{CityDAO, ContinentDAO, CountryDAO}
import javax.inject.Inject

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

class UtilServiceImpl  @Inject()(cityDAO : CityDAO) extends UtilService {
  override def removeCitiesOfCountry(countryIds: List[Int]): Option[String] = {
    val cities = ListBuffer.empty++= Await.result(cityDAO.getAllCities(),5.seconds)
    cities.find(city => countryIds.contains(city.countryId)) match {
      case Some(value) => cities.filterInPlace(city => !countryIds.contains(city.countryId))
        cityDAO.deleteCitiesByCountry(countryIds)
        Some("cities")
      case None => None
    }
  }

  override def removeCitiesOfCountry(countryId: Int): Option[String] = {
    val cities = ListBuffer.empty++= Await.result(cityDAO.getAllCities(),5.seconds)
    cities.find(_.countryId == countryId) match {
      case Some(value) => cities.filterInPlace(_.countryId != countryId)
        cityDAO.deleteCitiesByCountry(countryId)
        Some(" and cities of country ")
      case None => None
    }
  }
}
