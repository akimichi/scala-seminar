import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter04Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 4."){
    val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
    
    describe("sec 4.1"){
      it("Mapを作成する"){
        scores should equal {
          Map(("Alice", 10), ("Bob", 3), ("Cindy", 8))
        }
      }
    }
    describe("sec 4.2"){
      it("関数適用のかたちでMapにアクセスする"){
        scores("Bob") should equal(3)
      }
      it("マッチする要素がなければ、java.util.NoSuchElementException例外が返る"){
        intercept[java.util.NoSuchElementException]{
          scores("Sam") should equal(3)
        }
      }
    }
    describe("sec 4.3"){
    }
    describe("sec 4.4"){
    }
    describe("sec 4.5"){
    }
    describe("sec 4.6"){
      it("javaと連携する"){
      }
    }
    describe("Mapの説明を補足する"){
      val sample_map = Map(1 -> "value1", 2 -> "value2")
      it("Map#valuesで値のみを取りだす"){
        sample_map.values.toList should equal(List("value1","value2"))
      }
      it("Map#keysでキーのみを取りだす"){
        sample_map.keys.toList should equal(List(1,2))
      }
      it("toListでMapをListに変換する") {
        sample_map.toList should equal(List((1,"value1"), (2,"value2")))
      }
      /*
      it("foreachとパターンマッチで要素を取り出す") {
        sample_map.foreach { case (key,value) =>
          key match {
            case (term,lang) if term == "at0000" => {
              lang should equal("jp")
            }
            case _ => {}
          }
        }
      }
      it("forとパターンマッチで要素を取り出す") {
        for {
          (key, value) <- fixture.terms if key._1 == "at0001"
        } {
          key._2 should equal("en")
        }
        info("タプルを入れ子にして取り出せる")
        for {
          ((code,lang), value) <- fixture.terms if code == "at0001"
        } {
          lang should equal("en")
        }
        it("foreachで要素を繰り返し処理する") {
          fixture.simple_map.foreach { case (key,value) =>
            value should equal("value%s".format(key))
          }
        }
        
      }
      */
      it("varと+=演算子を用いて、immutable なMapに要素を追加する") {
        var map = Map("ja" -> "テキスト")
        map += ("en" -> "text")
        map.get("en") should equal(Some("text"))
      }
    }

    describe("sec 4.7"){
      it("Tuple"){
      }
    }
    describe("sec 4.8"){
      it("zipする"){
      }
    }
  }
}
