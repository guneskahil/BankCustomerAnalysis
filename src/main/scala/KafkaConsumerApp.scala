import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import java.util.{Properties}
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.JavaConverters._

object KafkaConsumerApp {
  def main(args: Array[String]): Unit = {
    // Kafka yapılandırma özellikleri
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("group.id", "test-consumer-group-new") // Yeni bir group ID kullanın
    props.put("key.deserializer", classOf[StringDeserializer].getName)
    props.put("value.deserializer", classOf[StringDeserializer].getName)
    props.put("auto.offset.reset", "earliest") // Eski mesajları okumak için
    props.put("enable.auto.commit", "false") // Otomatik commit'i kapatma

    // Kafka Consumer oluşturma
    val consumer = new KafkaConsumer[String, String](props)

    // Dinlenecek Kafka topic'i
    val topic = "newtopic"

    // Topic'e abone olma
    consumer.subscribe(List(topic).asJava)

    println(s"Consumer '${props.getProperty("group.id")}' topic '${topic}'i dinliyor...")

    // Dosya oluşturma veya açma
    val file = new File("consumer_output.json")
    val writer = new BufferedWriter(new FileWriter(file, true))

    try {
      while (true) {
        // Kafka'dan mesajları alma
        val records = consumer.poll(1000) // 1 saniye bekleme süresi, ihtiyaca göre ayarlayabilirsiniz

        if (!records.isEmpty) {
          for (record <- records.asScala) {
            println(s"Mesaj Alındı: Key = ${record.key()}, Value = ${record.value()}")
            writer.write(record.value()) // Veriyi dosyaya yaz
            writer.newLine()
          }
          writer.flush() // Dosyaya yazılan veriyi hemen kaydet
          consumer.commitSync() // İşlenen mesajları commit et
        } else {
          println("Hiç mesaj alınmadı, bekleniyor...")
        }
      }
    } catch {
      case e: Exception =>
        println(s"Bir hata oluştu: ${e.getMessage}")
    } finally {
      // Consumer'ı ve writer'ı düzgün şekilde kapatmak
      writer.close()
      consumer.close()
    }
  }
}
