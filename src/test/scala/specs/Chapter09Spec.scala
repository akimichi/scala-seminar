package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter09Spec extends FunSpec with ShouldMatchers with helpers {
  describe("case文で正規表現をmatchさせる") {
    import scala.util.matching.Regex

    it("年月日をmatchさせる"){
      val pattern:Regex = """^([0-9]+)-([0-9][0-9])-([0-9][0-9])$""".r
      "2013-06-28" match {
        case pattern(year,month,day) => {
          year should equal("2013")
          month should equal("06")
          day should equal("28")
        }
        case _ => fail()
      }
    }
  }
}
