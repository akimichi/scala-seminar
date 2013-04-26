import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class SeminarSpec extends FunSpec with ShouldMatchers with BeforeAndAfterAll with helpers {
  describe("Introduction"){
    describe("Futureによる非同期並列実行"){
      import akka.actor._
      import akka.actor.Actor._
      import akka.dispatch.{Await, ExecutionContext, Future}
      import java.util.concurrent.{TimeoutException, Executors}
      import System.{currentTimeMillis => now}
      
      val executorService = Executors.newFixedThreadPool(8)
      implicit val context = ExecutionContext.fromExecutor(executorService)

      it("非同期だが並列実行ではない"){
        val start = now
        val futures = for {
          a <- Future{ExpensiveCalc.calculatePiFor(0,1000000000)}
          b <- Future{ExpensiveCalc.calculatePiFor(0,1000000000)}
          c <- Future{ExpensiveCalc.calculatePiFor(0,1000000000)}
        } yield (a + b + c)/3
        futures onComplete { result =>
          result match {
            case Right(the_result) => {
              println("非同期だが並列実行ではない: %f sec".format((now - start)/1000.0))
              the_result should equal(3.1415926525880504)
            }
            case Left(ex) => fail(ex)
          }
        }
      }
      // 28.379
      it("非同期かつ並列実行である"){
        val start = now
        val listOfFutures:List[Future[Double]] = List(Future{ExpensiveCalc.calculatePiFor(0,1000000000)}.mapTo[Double],
                                                      Future{ExpensiveCalc.calculatePiFor(0,1000000000)}.mapTo[Double],
                                                      Future{ExpensiveCalc.calculatePiFor(0,1000000000)}.mapTo[Double])
        Future.fold(listOfFutures)(0.0)(_ + _) onComplete { result =>
          result match {
            case Right(the_result) => {
              println("非同期かつ並列実行である: %f sec".format((now - start)/1000.0))
              the_result/3 should equal(3.1415926525880504)
            }
            case Left(ex) => fail(ex)
          }
        }
      }
      //12.339
    }
    describe("自前のListを定義する"){
      object test {
        trait MyList[+T] {
          def isEmpty: Boolean
          def head: T
          def tail: MyList[T]
          def ::[U>:T](item: U): MyList[U]
          def map[U](f: T => U): MyList[U] = if (isEmpty) MyNil
                                             else f(head) :: tail.map(f)
          def last: T = {
            @annotation.tailrec
            def recursive(list:MyList[T]):T = {
              if(list.tail.isEmpty)
                list.head
              else {
               recursive(list.tail)
              }
            }
            recursive(this)
          }
          def foreach(f: T => Unit): Unit = {
            if(isEmpty)
              return
            else {
              f(head)
              tail.foreach(f)
            }
          }
        }
        case class MyListImpl[T](val head: T, val tail: MyList[T]) extends MyList[T] {
          def ::[U>:T](item: U): MyList[U] = new MyListImpl(item, this)
          def isEmpty = false
        }
        case object MyNil extends MyList[Nothing] {
          def ::[U>:Nothing](item: U): MyList[U] = MyListImpl(item, MyNil)
          override def isEmpty = true
          def head: Nothing = throw new NoSuchElementException("no head in empty list")
          def tail: MyList[Nothing] = throw new NoSuchElementException("no tail in empty list")
        }
      }
      it("MyListを使う"){
        import test._
        val mylist =  "ABC" :: "XYZ" :: "123" :: MyNil
        mylist.head should equal("ABC")
        mylist.tail should equal("XYZ" :: "123" :: MyNil)
        mylist.last should equal("123")
      }
    }
  }
  describe("環境設定"){
    describe("typesafe stackの導入"){
    }
    describe("sbtについて"){
      describe("sbtとgiter8でサンプルプロジェクトを作る") {
        info("brew install giter8")
        info("g8 akimichi/scala-sbt.g8")
        info("sbt test")
        info("sbt console")
        info("import sample._")
      }
      describe("consoleの紹介"){
        info(":keybindingでキー一覧がでる")
        info("!でヒストリー機能のコマンド一覧がでる")
        info("target/.historyファイルにヒストリーが記録されている")
      }
      describe("カスタマイズ"){
        info("project/*Build.scalaに設定を記述する")
      }
    }
  }
  describe("Chap 1."){
    describe("Sec 1.1"){
      it("5 * 8 + 2 = 42"){
        5 * 8 + 2 should equal {
          42
        }
      }
    }
    describe("Sec 1.2"){
      it("val で代入された変数は、再代入ができない"){
        val answer = 5 * 8 + 2
        answer should equal {
          42
        }
      }
      it("var で代入された変数は、再代入が可能である"){
        var answer = 5 * 8 + 2
        answer should equal {
          42
        }
        answer = 0
        answer should equal {
          0
        }
      }
      it("valの不変性:メンバー変数の例"){
        class ClassWithImmutableMember(val immutableMember:String)
        val immutableInstanceWithImmutableMember = new ClassWithImmutableMember("initial")
        immutableInstanceWithImmutableMember.immutableMember should equal("initial")
        info("""immutableInstanceWithImmutableMember.immutableMember = "mutated" は、 "reassignment to val" でコンパイルエラーとなる""")

        class ClassWithMutableMember(var mutableMember:String)
        val immutableInstanceWithMutableMember = new ClassWithMutableMember("initial")
        immutableInstanceWithMutableMember.mutableMember should equal("initial")
        immutableInstanceWithMutableMember.mutableMember = "mutated"
        info("不変な変数であっても、参照先が不変なインスタンスでなければ、参照先インスタンスの内容を更新することは可能である")
        immutableInstanceWithMutableMember.mutableMember should equal("mutated")
      }
      // it("valの不変性:コレクション Vectorの例"){
      //   val vector = Vector(1, 2, 3)
      //   val updated_vector = vector.updated(2, 4)
      //   updated_vector should equal(Vector(1, 2, 4)) // Vector(1, 2, 4)
      //   vector should equal(Vector(1, 2, 3))
      // }
      describe("不変コレクションの更新"){
        it("valで、immutable なリストに要素を追加する") {
          val list = List(1,2,3)
          val new_list = 0 :: list
          new_list.tail should equal(list)
          new_list.isInstanceOf[collection.immutable.List[_]] should equal(true)
          new_list(0) should equal(0)
          val another_list = List(1,2,3)
          System.identityHashCode(list) should not equal{ System.identityHashCode(another_list) }
          System.identityHashCode(new_list.tail) should equal{ System.identityHashCode(list) }
        }
        it("varと::=演算子を用いて、immutable なリストに要素を追加する") {
          var list = List(1,2,3)
          list ::= 0
          list.isInstanceOf[collection.immutable.List[_]] should equal(true)
          list(0) should equal(0)
        }
      }
    }
    describe("Sec 1.3"){
      it("Javaのプリミティブ型は、Scalaではクラスにラッパーされている"){
        {
          info("ラッピングはコンパイル時に暗黙的に実行される")
          1.toString
        } should equal("1")

        
        {
          "This is a string".size
        } should equal(16)

        1.to(10) should equal{
          Range(1,11)
        }
        info("実習: ここで console 上の補完機能でString型やInt型に定義されている各種のメソッドを調べる")
      }
    }
    describe("Sec 1.4"){
      it("Scalaでは演算子もメソッドである"){
        {
          1 + 2
        } should equal { 1.+(2) }
        
        {
          val x:BigInt = 1234567890
          (x * x * x).toString
        } should equal {
          "1881676371789154860897069000"
        }
      }
    }
    describe("Sec 1.5"){
      it("メソッドや関数を呼びだす"){
        {
          scala.math.sqrt(2) 
        } should equal { 1.4142135623730951 }
        
        {
          scala.math.pow(2,4)
        } should equal { 16.0 }
        
        {
          scala.math.min(3, scala.math.Pi)
        } should equal { 3.0 }

        {
          info("importすれば、先頭のパッケージ名を明示的に指定する必要がなくなる")
          info("アンダースコア _ は、scalaではワイルドカードの意味を持つことが多い")
          import scala.math._
          sqrt(2) should equal { 1.4142135623730951 }
        }
      }
      
      it("scalaには静的メソッドはない。かわりにシングルトンオブジェクトを使う"){
        object SingletonObject {
          def method:Int = 0
        }
        SingletonObject.method should equal(0)
      }
      it("引数のないメソッドの呼び出しは、()が不要である"){
        {
          "Hello".distinct
        } should equal { "Helo" }
      }
    }
    describe("Sec 1.6"){
      it("applyメソッドは関数適用の形でも呼出すことができる"){
        "Hello".apply(4) should equal{'o'}
        "Hello"(4) should equal{'o'}
      }
      it("applyメソッドは、コンパニオンオブジェクトにおいてファクトリーパターンを実現するときによく用いられる"){
        trait File {
          val name:String
          val content:String = name + "'s content"
        }
        class JsonFile(val name:String) extends File
        class XmlFile(val name:String) extends File
        class DocFile(val name:String) extends File
        
        object File {
          def apply(name:String):File = {
            name match {
              case "json" => new JsonFile("json")
              case "xml" => new XmlFile("xml")
              case "doc" => new DocFile("doc")
            }
          }
        }
        File("json").isInstanceOf[JsonFile] should equal(true)
        File("xml").isInstanceOf[XmlFile] should equal(true)
        File("json").content should equal("json's content")
      }
    }
    describe("Sec 1.7"){
      info("http://www.scala-lang.org/api/current/index.html#package")
      describe("Seqを試す"){
        it("++") {
          Seq(1,2,3) ++ Seq(4,5,6) should equal {
            Seq(1,2,3,4,5,6)
          }
        }
        it(":\\[B](z: B)(op: (A, B) ⇒ B): B") {
          val seq = Seq("a","ab","abc","abcd")
          (seq :\ 0)((a,b) => a.length + b) should equal{
            10
          }
        }
      }
    }
  }
}

    

