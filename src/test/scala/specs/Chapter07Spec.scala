package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter07Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 7."){
    describe("sec 7.1"){
      /*
      package com {
        package horstmann {
          package impatient {
            class Employee
            
          }
        }
      }
      */ 
    }
    describe("sec 7.5"){
      info("package は、class,object,traitしか定義できないが、package object ではこれらに加えてメソッドや変数も定義できる")
    }
    describe("sec 7.6"){
      class Person(val name:String) {
        private[test] def description = "A person with name " + name
      }
    }
    describe("sec 7.7"){
      info("_を末尾に付けると、それ以下の全メンバーがimportされる")
      import java.awt.Color._
      val c1 = RED // Color.RED
      val c2 = decode("#ff0000") // Color.decode
    }
    describe("sec 7.8"){
      info("import文はどこにでも置くことができる。")
      class Manager {
        import scala.collection.mutable._
        val subordinates = new ArrayBuffer[Int]
        def a_method = {
          import java.util.HashMap
          {}
        }
      }
    }
    describe("sec 7.9"){
      it("パッケージ名を改名する"){
        import java.awt.{Color, Font}
        import java.util.{HashMap => JavaHashMap}
        import scala.collection.mutable._

        {}
      }
      it("一部のパッケージを不可視化する"){
        import java.util.{HashMap => _, _}
        import scala.collection.mutable._

        {}
      }
    }
    describe("sec 7.10"){
      /*
       * 以下のパッケージはデフォルトで読みこまれる
       * 
       * import java.lang._
       * import scala._
       * import Predef._
       *
       */ 
    }
  }
}
