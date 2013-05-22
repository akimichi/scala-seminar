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
      it("mutableなMapのデータを更新する"){
        val scores = collection.mutable.Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
        info("valで変数が宣言されている点に注意")
        scores("Bob") should equal(3)
        scores("Bob") = 10
        scores("Bob") should equal(10)
        scores += ("Bob" -> 10, "Fred" -> 7)
        scores should equal {
          collection.mutable.Map("Alice" -> 10, "Bob" -> 10, "Cindy" -> 8, "Fred" -> 7)
        }
        scores -= "Alice"
        scores should equal {
          collection.mutable.Map("Bob" -> 10, "Cindy" -> 8, "Fred" -> 7)
        }
      }
      it("immmutableなMapのデータを更新する"){
        val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
        val newScores = scores + ("Bob" -> 10, "Fred" -> 7) // New map with update
        newScores should equal {
          collection.mutable.Map("Alice" -> 10,"Bob" -> 10, "Cindy" -> 8, "Fred" -> 7)
        }
      }
      it("varと+=演算子を用いて、immutable なMapを更新する") {
        var scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
        scores = scores + ("Bob" -> 10, "Fred" -> 7)
        scores should equal {
          collection.mutable.Map("Alice" -> 10,"Bob" -> 10, "Cindy" -> 8, "Fred" -> 7)
        }
        info("-演算子でMapからデータを削除する")
        scores = scores - "Alice"
        scores should equal {
          collection.mutable.Map("Bob" -> 10, "Cindy" -> 8, "Fred" -> 7)
        }
      }
    }
    describe("sec 4.4"){
      val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
      it("forでMapを反復処理する"){
        for {
          (key, value) <- scores
        } {
          key match {
            case "Alice" => value should equal(10)
            case "Bob" => value should equal(3)
            case "Cindy" => value should equal(8)
            case _ => fail()
          }
        }
      }
      it("keySetでキーのセットを取得する"){
        scores.keySet should equal(Set("Alice", "Bob", "Cindy"))
      }
      it("Mapのキーと値を交換する"){
        val reveresed = for ((k, v) <- scores) yield (v, k)
        reveresed should equal {
          Map(10 -> "Alice", 3 -> "Bob", 8 -> "Cindy")
        }
      }
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
