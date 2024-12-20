import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Flatten, Dense
from tensorflow.keras.datasets import cifar10
from tensorflow.keras.utils import to_categorical

# CIFAR-10 veri setini yükleyin
(train_images, train_labels), (test_images, test_labels) = cifar10.load_data()

# Veriyi ölçeklendirme (0-255 aralığından 0-1 aralığına)
train_images = train_images.astype('float32') / 255.0
test_images = test_images.astype('float32') / 255.0

# Etiketleri kategorik hale getirme
train_labels = to_categorical(train_labels, 10)
test_labels = to_categorical(test_labels, 10)

# CNN modelini oluşturma
model = Sequential([
    Conv2D(32, (3, 3), activation='relu', input_shape=(32, 32, 3)),
    MaxPooling2D(2, 2),
    Conv2D(64, (3, 3), activation='relu'),
    MaxPooling2D(2, 2),
    Flatten(),
    Dense(64, activation='relu'),
    Dense(10, activation='softmax')  # 10 sınıf için çıkış
])

# Modeli derleme
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

# Modeli eğitme
model.fit(train_images, train_labels, epochs=10, batch_size=64)

# Model ile test verisi üzerinde tahmin yapma
test_loss, test_acc = model.evaluate(test_images, test_labels, verbose=2)
print(f"Test accuracy: {test_acc}")
