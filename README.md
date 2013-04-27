scala-seminar
=============

環境の導入
--------

このセミナーでは、typesafe stack で提供されている scala と sbt を利用する。
そこでまずは、typesafe stack をそれぞれの環境にインストールする必要がある。

1. typesafe stack のインストール
   http://typesafe.com/stack/download 
   1. Macの場合

      > brew installs Scala sbt maven giter8
   1. Ubuntu/Debian の場合

      > $ apt-get update

      > $ apt-get install typesafe-stack
2. github からの本レポジトリのクローン

   > git clone https://github.com/akimichi/scala-seminar.git

   > cd scala-seminar

sbtの使い方
---------
セミナーで使うコマンドは、以下の2,3個にすぎない。

テストを実行する。
> sbt test

REPLを起動する。
> sbt console

