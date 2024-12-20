package models

import java.io._
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.{VectorAssembler, StringIndexer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object RandomForestModel {
  def main(args: Array[String]): Unit = {
    // SparkSession oluşturma
    val spark = SparkSession.builder()
      .appName("RandomForestModel")
      .master("local[*]")
      .getOrCreate()

    // Veri setini yükleme
    val filePath = "C:/Users/gunes/Desktop/Veriseti/veriseti_islenmis.csv"
    var df = spark.read.option("header", "true").csv(filePath)

    // NULL değerlerini içeren satırları kaldırma
    df = df.na.drop()

    // Sayısal sütunları belirleme ve dönüşüm
    val numericColumns = Array("age", "balance", "day", "duration", "campaign", "pdays", "previous")
    numericColumns.foreach { colName =>
      df = df.withColumn(colName, col(colName).cast(DoubleType))
    }

    // Fazla benzersiz değere sahip kategorik sütunları filtreleme
    val categoricalColumns = df.columns.filter(c => df.schema(c).dataType == StringType)
    val maxDistinctThreshold = 100  // En fazla 100 benzersiz değere sahip sütunları tut

    val filteredCategoricalColumns = categoricalColumns.filter { colName =>
      val distinctCount = df.select(colName).distinct().count()
      println(s"$colName: $distinctCount benzersiz değer")  // Kontrol amaçlı yazdırma
      distinctCount <= maxDistinctThreshold
    }

    // StringIndexer ile kategorik sütunları dönüştürme
    var indexedData = df
    filteredCategoricalColumns.foreach { colName =>
      val indexer = new StringIndexer()
        .setInputCol(colName)
        .setOutputCol(colName + "_indexed")
        .setHandleInvalid("skip")  // Geçersiz değerleri atlamak için "skip"
      indexedData = indexer.fit(indexedData).transform(indexedData)
    }

    // Özellik sütunlarını birleştirme
    val assembler = new VectorAssembler()
      .setInputCols(numericColumns ++ filteredCategoricalColumns.map(_ + "_indexed"))
      .setOutputCol("features")

    val assembledData = assembler.transform(indexedData)

    // Hedef sütununu 'y' olarak belirleyip label olarak kullanmak
    val labelIndexer = new StringIndexer().setInputCol("y").setOutputCol("label")
    val finalData = labelIndexer.fit(assembledData).transform(assembledData)

    // Eğitim ve test setlerine ayırma
    val Array(trainingData, testData) = finalData.randomSplit(Array(0.7, 0.3), seed = 42)

    // Random Forest Modeli
    val rf = new RandomForestClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setMaxBins(100)

    val rfModel = rf.fit(trainingData)
    val predictions = rfModel.transform(testData)

    // TP, TN, FP, FN hesaplama
    val tp = predictions.filter("prediction == 1.0 AND label == 1.0").count()
    val tn = predictions.filter("prediction == 0.0 AND label == 0.0").count()
    val fp = predictions.filter("prediction == 1.0 AND label == 0.0").count()
    val fn = predictions.filter("prediction == 0.0 AND label == 1.0").count()

    // Metrikleri hesaplama
    val accuracy = (tp + tn).toDouble / (tp + tn + fp + fn)
    val precision = if (tp + fp > 0) tp.toDouble / (tp + fp) else 0.0
    val recall = if (tp + fn > 0) tp.toDouble / (tp + fn) else 0.0
    val f1Score = if (precision + recall > 0) 2 * ((precision * recall) / (precision + recall)) else 0.0
    val errorRate = (fp + fn).toDouble / (tp + tn + fp + fn)

    // Sonuçları yazdırma
    println(s"True Positive (TP): $tp")
    println(s"True Negative (TN): $tn")
    println(s"False Positive (FP): $fp")
    println(s"False Negative (FN): $fn")
    println(f"Doğruluk (Accuracy): $accuracy%.4f")
    println(f"Kesinlik (Precision): $precision%.4f")
    println(f"Duyarlılık (Recall): $recall%.4f")
    println(f"F1 Skoru (F1 Score): $f1Score%.4f")
    println(f"Hata Oranı (Error Rate): $errorRate%.4f")

    // Confusion Matrix
    predictions.groupBy("label", "prediction").count().show()

    // Sonuçları CSV dosyasına yazma
    val writer = new PrintWriter(new File("model_metrics_random_forest.csv"))
    writer.write("metric,value\n")
    writer.write(s"True Positive (TP),$tp\n")
    writer.write(s"True Negative (TN),$tn\n")
    writer.write(s"False Positive (FP),$fp\n")
    writer.write(s"False Negative (FN),$fn\n")
    writer.write(s"accuracy,$accuracy\n")
    writer.write(s"precision,$precision\n")
    writer.write(s"recall,$recall\n")
    writer.write(s"f1_score,$f1Score\n")
    writer.write(s"error_rate,$errorRate\n")
    writer.close()

    // SparkSession kapatma
    spark.stop()
  }
}
