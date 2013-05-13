scala-seminar
=============

環境の導入
--------

このセミナーでは、typesafe stack で提供されている scala と sbt を利用する。
そこでまずは、typesafe stack をそれぞれの環境にインストールする必要がある。

1. typesafe stack activator のインストール
   http://typesafe.com/platform/getstarted から typesafe activator をダウンロードする。

   > $ wget http://downloads.typesafe.com/typesafe-activator/{VERSION}/typesafe-activator-{VERSION}.zip

   現時点では、VERSION は 0.1.1 なので、以下のコマンドでダウンロードできる。

   > $ wget http://downloads.typesafe.com/typesafe-activator/{VERSION}/typesafe-activator-0.1.1.zip
   
   > $ unzip typesafe-activator-{VERSION}.zip

   > $ cd activator-{VERSION}

   > $ ./activator ui

2. github からの本レポジトリのクローン

   > $ git clone https://github.com/akimichi/scala-seminar.git

   > $ cd scala-seminar

activatorの使い方
---------
セミナーで使うコマンドは、以下の2,3個にすぎない。

scala-seminar のディレクトリにて、activator を起動する。

   > $ ./activator

testを実行する。

   > scala-seminar>  test

REPLを実行する。

   > scala-seminar>  console
  

