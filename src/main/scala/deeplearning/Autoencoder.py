from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense

# Autoencoder için giriş boyutları
input_dim = 784  # Örneğin, MNIST veriseti
encoding_dim = 32  # Kodlama boyutu

# Encoder
input_layer = Input(shape=(input_dim,))
encoded = Dense(encoding_dim, activation='relu')(input_layer)

# Decoder
decoded = Dense(input_dim, activation='sigmoid')(encoded)

# Autoencoder modeli
autoencoder = Model(input_layer, decoded)

# Encoder modeli
encoder = Model(input_layer, encoded)

# Decoder modeli
encoded_input = Input(shape=(encoding_dim,))
decoder_layer = autoencoder.layers[-1]
decoder = Model(encoded_input, decoder_layer(encoded_input))

# Modeli derleme
autoencoder.compile(optimizer='adam', loss='binary_crossentropy')

# Modeli eğitme
autoencoder.fit(x_train, x_train, epochs=50, batch_size=256, shuffle=True, validation_data=(x_test, x_test))
