import numpy as np
import pandas as pd
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense
from tensorflow.keras.preprocessing.sequence import TimeseriesGenerator

# Zaman serisi verisini yükleme
data = pd.read_csv('data.csv')
sequence_length = 50

# Zaman serisi verisini oluşturma
generator = TimeseriesGenerator(data.values, data.values, length=sequence_length, batch_size=32)

# LSTM Modeli
model = Sequential()
model.add(LSTM(64, activation='relu', input_shape=(sequence_length, data.shape[1])))
model.add(Dense(1))
model.compile(optimizer='adam', loss='mse')

# Modeli eğitme
model.fit(generator, epochs=20)

# Model ile tahmin yapma
predictions = model.predict(generator)
