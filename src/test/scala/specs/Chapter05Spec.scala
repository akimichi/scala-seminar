import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter05Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 5."){
    describe("sec 5.1"){
      it("クラスからインスタンスを作成する"){
        class Counter {
          private var value = 0 // You must initialize the field
          def increment() { value += 1 } // Methods are public by default
          def current() = value
        }
        val myCounter = new Counter
        myCounter.increment()
        myCounter.current should equal(1)
      }
    }
    describe("sec 5.2"){
      
    }
  }
}
