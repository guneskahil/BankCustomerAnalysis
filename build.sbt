ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.11.8"

lazy val root = (project in file("."))
  .settings(
    name := "untitled2"
  )

libraryDependencies ++= Seq(
  // Spark Core library
  "org.apache.spark" %% "spark-core" % "2.3.1",

  // Spark SQL library
  "org.apache.spark" %% "spark-sql" % "2.3.1",

  // Spark SQL Kafka Connector
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.1",

  // Spark Streaming library
  "org.apache.spark" %% "spark-streaming" % "2.3.1",

  // Spark Streaming Kafka Connector
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.1",

  // Kafka Client
  "org.apache.kafka" % "kafka-clients" % "0.10.0.0",

  // SLF4J Logging
  "org.slf4j" % "slf4j-api" % "1.7.32",
  "org.slf4j" % "slf4j-log4j12" % "1.7.32",

  // Spark MLlib for machine learning
  "org.apache.spark" %% "spark-mllib" % "2.3.1"
)
