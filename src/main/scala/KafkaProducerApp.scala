import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, Callback, RecordMetadata}
import scala.io.Source
import java.util.Properties

object KafkaProducerApp {
  def main(args: Array[String]): Unit = {
    // Kafka yapılandırma özellikleri
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092") // Kafka sunucunuzun adresi
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "all") // Tüm broker'lar mesajı aldığında onayla
    props.put("retries", "3") // Tekrar deneme sayısı
    props.put("linger.ms", "1") // Mesajları birleştirme için bekleme süresi
    props.put("batch.size", "16384") // Mesajları gruplama boyutu

    // Kafka producer oluşturma
    val producer = new KafkaProducer[String, String](props)

    // Veri setinin dosya yolu
    val filename = "C:/Users/gunes/Desktop/Veriseti/veriseti_islenmis.csv" // Buradaki dosya yolunu doğru şekilde belirtin

    try {
      val source = Source.fromFile(filename)
      val lines = source.getLines().drop(1).toList // Başlık satırını atla

      // Gönderim yapılacak Kafka topic'i
      val topic = "newtopic" // Topic adını ihtiyacınıza göre ayarlayın

      lines.foreach { line =>
        val dataFields = line.split(",").map(_.trim) // Virgül ile ayır ve boşlukları temizle

        // Veri satırının beklenen uzunlukta olup olmadığını kontrol et
        if (dataFields.length == 17) { // Satırdaki alan sayısının 17 olduğundan emin olun
          // JSON formatında veri oluşturma
          val jsonData =
            s"""{
               |"age": "${dataFields(0)}",
               |"job": "${dataFields(1)}",
               |"marital": "${dataFields(2)}",
               |"education": "${dataFields(3)}",
               |"default": "${dataFields(4)}",
               |"balance": "${dataFields(5)}",
               |"housing": "${dataFields(6)}",
               |"loan": "${dataFields(7)}",
               |"contact": "${dataFields(8)}",
               |"day": "${dataFields(9)}",
               |"month": "${dataFields(10)}",
               |"duration": "${dataFields(11)}",
               |"campaign": "${dataFields(12)}",
               |"pdays": "${dataFields(13)}",
               |"previous": "${dataFields(14)}",
               |"poutcome": "${dataFields(15)}",
               |"y": "${dataFields(16)}"
               |}""".stripMargin

          // Kafka'ya veri gönderme
          val record = new ProducerRecord[String, String](topic, jsonData)

          // Asenkron send işlemi
          producer.send(record, new Callback {
            override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
              if (exception != null) {
                println(s"Hata oluştu: ${exception.getMessage}")
              } else {
                println(s"Mesaj başarıyla gönderildi: ${metadata.topic()} | ${metadata.partition()} | ${metadata.offset()}")
              }
            }
          })
        } else {
          println(s"Yetersiz veri: ${line}")
        }
      }

      // Tüm mesajlar gönderildikten sonra producer'ı kapatmak için flush işlemi
      producer.flush()
      println(s"Tüm veriler başarıyla '${topic}' topic'ine gönderildi.")
    } catch {
      case e: Exception =>
        println(s"Dosya okunurken hata oluştu: ${e.getMessage}")
    } finally {
      // Kaynakları serbest bırakma
      producer.close()
    }
  }
}
