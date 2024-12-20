import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._

object SparkKafkaStreamProcessor {
  def main(args: Array[String]): Unit = {
    // Spark Session oluşturma
    val spark = SparkSession.builder
      .appName("Kafka Spark Streaming Anomaly Processor")
      .master("local[*]") // Local ortamda çalıştırma
      .getOrCreate()

    // Kafka'dan veri okuma
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092") // Kafka broker adresi
      .option("subscribe", "newtopic") // Kafka'dan alınacak topic
      .option("startingOffsets", "earliest") // İlk offsetten başla
      .load()

    // Kafka verilerini String'e dönüştürme
    val dataDF = kafkaDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

    // JSON şemasını tanımlama
    val schema = StructType(Array(
      StructField("age", StringType, true),
      StructField("balance", StringType, true)
    ))

    // 'value' kolonundaki JSON verisini ayrıştırma
    val jsonDF = dataDF.withColumn("json_data", from_json(col("value"), schema))

    // JSON içeriğinden balance ve age sütunlarını al
    val processedDF = jsonDF
      .withColumn("balance", col("json_data.balance").cast("int"))
      .withColumn("age", col("json_data.age").cast("int"))

    // Anomali tespiti: balance > 1000 ve age > 35
    val anomalyDF = processedDF.withColumn("anomaly",
      when(col("balance") > 1000 && col("age") > 35, true).otherwise(false)
    )

    // Anomali olan verileri filtreleme
    val filteredAnomaliesDF = anomalyDF.filter(col("anomaly") === true)
      .select(col("balance"), col("age"))

    // JSON formatında yazılacak çıktı yolu
    val outputPath = "C:/Users/gunes/Desktop/Veriseti/output/anomalili_veri"

    // Query'i başlatma ve veriyi JSON formatında yazma
    val query = filteredAnomaliesDF.writeStream
      .outputMode("append")
      .format("json") // JSON formatında çıktı
      .option("checkpointLocation", "C:/Users/gunes/Desktop/Veriseti/checkpoint") // Checkpoint yolu
      .option("path", outputPath) // Çıktı yolu
      .trigger(Trigger.ProcessingTime("10 seconds")) // 10 saniyede bir çalıştırma
      .start()

    // Akışın sonlanmasını bekle
    query.awaitTermination()
  }
}
