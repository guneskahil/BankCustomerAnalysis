# BankCustomerAnalysis

Bu proje, Apache Spark ve Apache Kafka entegrasyonu ile büyük veri üzerinde gerçek zamanlı anomali tespiti gerçekleştirmek üzere tasarlanmıştır.

---

## Proje Süreci

Proje, aşağıdaki adımları izleyerek tamamlanmıştır:

### 1. Veri Seti Seçimi ve Ön İşleme
- **Veri Seçimi**: Projede, banka müşteri bilgilerini içeren bir veri seti kullanılmıştır. Bu veri setinde anomali sütunu bulunmamaktadır fakat anomali koşulunu kendimiz belirleyip tespit etmek için yeterli çeşitliliğe ve boyuta sahiptir.
- **Ön İşleme**:
  - Eksik değerler dolduruldu veya veri setinden çıkarıldı.
  - Sayısal değerler normalize edildi ve standartlaştırıldı.
  - Kategorik veriler one-hot encoding yöntemiyle sayısal hale getirildi.
  - Aykırı değerler tespit edilerek uygun bir şekilde işlendi.
- **Veri Görselleştirme**: Veri özellikleri dağılım grafikleri, scatter plot ve korelasyon matrisleri ile analiz edilerek raporlandı.

### 2. Veri Görselleştirme ve Analiz
- Veri setinin genel yapısını anlamak için histogramlar ve scatter plot'lar çizildi.
- Elde edilen bulgular ve anomalilere dair öngörüler detaylı bir şekilde raporlandı.

### 3. Makine Öğrenimi Modeli Geliştirme
- **Model Seçimi**:
  - Random Forest, Decision Tree ve Logistic Regression gibi makine öğrenimi algoritmaları kullanıldı.
- **Model Değerlendirme**:
  - Accuracy, precision, recall ve F1 skoru gibi başarı ölçütleri ile modellerin performansları karşılaştırıldı.
  - En iyi performansı gösteren model seçilerek entegrasyon için hazırlandı.

### 4. Kafka ve Zookeeper Entegrasyonu
- **Zookeeper Kullanımı**: Kafka'nın cluster yönetimi ve topic koordinasyonu Zookeeper üzerinden sağlandı.
- **Kafka Producer**: Gerçek zamanlı veri üretimi sağlandı ve bu veriler belirli bir zaman diliminde modele gönderildi.
- **Kafka Consumer**: Model tarafından tespit edilen anomaliler ve normal veriler farklı Kafka topic'lerinde toplandı.
- **Spyder Kullanımı**: Python kodları, Spyder IDE üzerinde yazılıp çalıştırılarak Kafka ile Spark arasındaki veri akışı ve işleme gerçekleştirildi.

### 5. Spark Tabanlı Veri İşleme
- **Kafka ve Spark Entegrasyonu**: Kafka'dan gelen veriler, Spark Streaming ile işlendi.
- **Anomali Tespiti**: Spark MLlib ile eğitilen modeller, veriler üzerinde anomali tespiti yaptı. 
- **Sonuçlar**: Kafka topic'lerine geri gönderildi ve gerçek zamanlı olarak gözlemlendi.

---

## Kullanılan Teknolojiler
- **Apache Kafka**: Gerçek zamanlı veri akışı platformu.
- **Apache Zookeeper**: Kafka için cluster koordinasyonu sağlayan sistem.
- **Apache Spark**: Dağıtık veri işleme motoru.
- **Scala**: Model eğitimi için kullanılan programlama dili.
- **Python**: Veri işleme için kullanılan programlama dili.
- **Spyder**: Python kodlarının yazıldığı ve çalıştırıldığı IDE.
- **IntelliJ IDEA**: Scala kodlarının yazıldığı ve çalıştırıldığı IDE.

---

## Proje için Gereksinimler
- **Java**: 1.8 
- **Apache Spark**: 2.3.1 
- **Apache Kafka**: 0.10.0.0 
- **Zookeeper**
- **Python**: 3.7 
- **Scala**: 2.11.8 
- **IntelliJ IDEA** (Scala kodları için)
- **Spyder** (Python kodları için)
