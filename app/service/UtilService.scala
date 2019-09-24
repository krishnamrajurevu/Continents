package service

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[UtilServiceImpl])
trait UtilService {
  def removeCitiesOfCountry(countryIds: List[Int]): Option[String]

  def removeCitiesOfCountry(countryId: Int): Option[String]
}
