from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, col
from pyspark.sql.types import StructType, StringType, IntegerType

# SparkSession oluşturma
spark = SparkSession.builder \
    .appName("KafkaSparkStreaming") \
    .getOrCreate()

# Kafka'dan veri okuma
df = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe", "new_data") \
    .load()

# JSON verisini parse etme
schema = StructType() \
    .add("age", IntegerType()) \
    .add("job", StringType()) \
    .add("balance", IntegerType()) \
    .add("loan", StringType())

parsed_df = df.select(from_json(col("value").cast("string"), schema).alias("data")).select("data.*")

# Veriyi ekrana yazdırma
query = parsed_df.writeStream.outputMode("append").format("console").start()

query.awaitTermination()
