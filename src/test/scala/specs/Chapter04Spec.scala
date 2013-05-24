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
      it("SortedMap"){
        val scores = scala.collection.immutable.SortedMap("Alice" -> 10,"Fred" -> 7, "Bob" -> 3, "Cindy" -> 8)
        scores should equal{
          Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8, "Fred" -> 7)
        }
      }
    }
    describe("sec 4.6"){
      info("scalaとjavaのあいだのコレクション型の変換には、JavaConversionsとJavaConvertersの2種類がある")
      describe("JavaConversionsでjavaと連携する"){
        /*
         * Javaコレクションに対しScalaコレクションのメソッドを適用すれば、JavaコレクションからScalaコレクション（のラッパークラス）に暗黙に変換される
         */ 
        info("http://www.scala-lang.org/api/current/index.html#scala.collection.JavaConversions$")
        import scala.collection.JavaConversions._
        
        val props = System.getProperties()
        props("java.runtime.name") should not equal("")
      }
      describe("JavaConvertersでjavaと連携する"){
        /*
         * JavaConvertersオブジェクトのメソッドをインポートしておくと、変換メソッド（asScalaやasJava）が呼び出せるようになる。
         * すなわち、変換は明示的に行う必要がある。
         */ 
        info("http://www.scala-lang.org/api/current/index.html#scala.collection.JavaConverters$")
        import scala.collection.JavaConverters._

        it("asScalaメソッドで JavaのMapをScalaのMapに変換する"){
          val props = System.getProperties().asScala
          props("java.runtime.name") should not equal("")
        }
        it("asJavaメソッドで ScalaのListをJavaのListに変換する"){
          val codeList:java.util.List[String] = List("mean", "total").asJava
        }
        
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
    }

    describe("sec 4.7"){
      it("Tupleを操作する"){
        val tuple : Tuple3[Int,Double,String] = (1, 3.14, "Fred")
        tuple._1 should equal(1)
        tuple._2 should equal(3.14)

        info("パターンマッチングで個々の要素を変数に取り出したほうが、可読性がよい")
        val (first, second, third) = tuple
        first should equal(1)
        second should equal(3.14)
      }
      it("Tupleを返すメソッド"){
        "New York".partition(_.isUpper) // Yields the pair ("NY", "ew ork")
      }

      it("補足: for内包のなかでTupleの要素を取り出す") {
        type ID = Int
        type Lang = String
        type Title = String
        type Author = String
        type Book = Tuple2[Title,Lang]
        type Record = Tuple3[ID,Author, Book]
        val books : List[Record] = List((1,"Shakespeare",("King Lear","en")),
                                        (2,"Rousseau",("Emile","fr")),
                                        (3,"Chekov",("Three sisters","ru")))
        info("タプルを入れ子にして取り出せる")
        for {
          (id, author, (title,lang)) <- books if lang == "ru"
        } {
          author should equal("Chekov")
        }
      }
      it("補足: Tupleでのパターンマッチ"){
        type Person = Tuple3[String,Symbol,Int]
        val man : Person = ("foo",'Male,20)
       
        man match {
          case (name, 'Male, age) if age < 20 => fail()
          case (name, 'Female,age) if age >= 20  => fail()
          case (name, gender,age) => {
            name should equal("foo")
          }
        }
      }
      describe("発展"){
        type Name = String
        type Iden = Int
        type Mark = Int

        type Codes = List[(Name,Iden)]
        type Marks = List[(Iden,Mark)]

        val codes:Codes = List("ANDERSON" -> 101372,
                               "BAYLIS" -> 101369,
                               "CARTER" -> 101370,
                               "DENNIS" -> 101371,
                               "EDWARD" -> 101373)
        val marks:Marks = List(101369 -> 62,
                               101370 -> 75,
                               101371 -> 62,
                               101372 -> 30,
                               101373 -> 50)
                          
        def pair[A,B,C] : Pair[A => B, A => C] => A => Pair[B,C] = {(fg:Pair[A => B, A => C]) => { x:A =>
          val (f,g) : Pair[A => B, A => C] = fg
          (f(x), g(x))
        }}
        /* 
         cross(xs ys) = [(x, y) |  x <- xs, y <- ys]
         */ 
        def cross[A,B,C,D] : Pair[A => B, C => D] => Pair[A,C] => Pair[B,D] = {(fg:Pair[A => B, C => D]) => { ac:Pair[A,C] =>
          val (f,g) : Pair[A => B, C => D] = fg
          val (a,c) : Pair[A,C] = ac
          val (b,d) : Pair[B,D] = (f(a), g(c))
          (b,d)
        }}

        /*
        def collate(codes:Codes, marks:Marks) : List[Pair[Name,Mark]] = {
          val ids:List[Iden] = for {
            code <- codes
            mark <- marks
            
          } yield {
            val ab : (Name,Iden) => Name = {(name,iden) =>
              name
            }
            val bd : (Iden, Mark) => Mark = {(iden,mark) =>
              mark
            }
            cross[(Name,Iden),Name,(Iden,Mark),Mark]((ab,bd))(code, mark)
          }
        }
        */ 
      }
    }
    describe("sec 4.8"){
      it("zipする"){
        val symbols = List("<", "-", ">")
        val counts = List(2, 10, 2)
        val pairs : List[Tuple2[String,Int]] = symbols.zip(counts)
        pairs should equal {
          List(("<", 2), ("-", 10), (">", 2))
        }
      }
    }
  }
}
