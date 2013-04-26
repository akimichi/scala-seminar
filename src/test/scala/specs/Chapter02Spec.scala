import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter02Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 2."){
    describe("Sec 2.1"){
      it("条件構造の if も式 expression である"){
        info("式 expression とは評価されると値を返すものであり、一方で文 statement とは値を返さずに副作用を生じるものである")
        var x = 0
        val s = if (x > 0) 1 else -1
        s should equal(-1)
        info("返り値を捨てれば文のように作用するが、好ましいスタイルではない")
        if (x > 0) {
          x = x
          x should be >= 0
        } else {
          x = - x
          x should be >= 0
        }
      }
      it("したがってif式も型 type を持つ"){
        info("値は常に何らかの型に帰属するものであり、式が常に何らかの値を返すものであれば、式は常に型を持つ")
        val x = 10
        val s = if (x > 0) 1 else -1
        s.isInstanceOf[Int] should equal(true)
      }
      it("演習: 次のif式はどのような型を持つ"){
        val x = "a string"
        val s = if (x.length > 0)
                  x.length
                else
                  "empty string"
        s.isInstanceOf[Any] should equal(true)
      }
    }
    
  }
}

