# Gerekli kütüphaneleri içe aktarma
import pandas as pd
from sklearn.preprocessing import StandardScaler, MinMaxScaler, LabelEncoder
import matplotlib.pyplot as plt
import seaborn as sns
from scipy import stats
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import GridSearchCV
from sklearn.linear_model import LogisticRegression

# Veri setini yükleme
try:
    df = pd.read_csv(r"C:/Users/gunes/Desktop/Veriseti/bank.csv", sep=";")
    print("Veri seti başarıyla yüklendi.")
except FileNotFoundError as e:
    print("Veri seti bulunamadı. Lütfen dosya yolunu kontrol edin:", e)
    exit()

# Kategorik sütunları sayısal değerlere dönüştürme (One-Hot Encoding)
df = pd.get_dummies(df, drop_first=True)

# Eksik verileri kontrol etme
print("Eksik Veriler (df öncesi):")
print(df.isnull().sum())

# Eksik verileri doldurma
categorical_cols = df.select_dtypes(include=['object']).columns
for col in categorical_cols:
    df[col] = df[col].fillna(df[col].mode()[0])  # Kategorik veriler için en yaygın değeri kullan

numerical_cols = df.select_dtypes(include=['float64', 'int64']).columns
df[numerical_cols] = df[numerical_cols].fillna(df[numerical_cols].mean())  # Sayısal veriler için ortalama değeri kullan

print("Eksik Veriler (df sonrası):")
print(df.isnull().sum())

# Eğitim seviyesini birleştirme ve kategorik kodlama
if 'education_secondary' in df.columns and 'education_tertiary' in df.columns:
    # Eğitim sütunlarını birleştirerek 'education' sütunu oluşturuluyor
    df['education'] = df[['education_secondary', 'education_tertiary', 'education_unknown']].idxmax(axis=1)
    df['education'] = df['education'].map({
        'education_secondary': 'Secondary',
        'education_tertiary': 'Tertiary',
        'education_unknown': 'Unknown'
    })
    label_encoder = LabelEncoder()
    df['education'] = label_encoder.fit_transform(df['education'])

    # Eğitimle ilgili eski sütunları kaldırıyoruz
    X = df.drop(['education_secondary', 'education_tertiary', 'education_unknown'], axis=1)
    y = df['education']
else:
    print("Eğitim sütunları bulunamadı. İşlem devam edemedi.")
    exit()

# Sayısal sütunları normalizasyon ve standartlaştırma
scaler_minmax = MinMaxScaler()
df[numerical_cols] = scaler_minmax.fit_transform(df[numerical_cols])

# Boxplot ile uç değer analizi
for col in numerical_cols:
    plt.figure(figsize=(8, 4))
    sns.boxplot(x=df[col])
    plt.title(f'{col} için Boxplot')
    plt.show()

# Z-score ile uç değerleri filtreleme
z_scores = stats.zscore(df[numerical_cols])
df = df[(abs(z_scores) < 3).all(axis=1)]  # Z-score > 3 olan veriler çıkarılır

# Korelasyon matrisi
plt.figure(figsize=(10, 8))
sns.heatmap(df[numerical_cols].corr(), annot=True, cmap='coolwarm')
plt.title('Korelasyon Matrisi')
plt.show()

# Eğitim ve test setine ayırma
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# Random Forest Modeli
model_rf = RandomForestClassifier(random_state=42)
model_rf.fit(X_train, y_train)
y_pred_rf = model_rf.predict(X_test)
accuracy_rf = accuracy_score(y_test, y_pred_rf)
print(f"Random Forest doğruluk oranı: {accuracy_rf * 100:.2f}%")

# Grid Search ile parametre optimizasyonu
param_grid = {
    'n_estimators': [50, 100, 200],
    'max_depth': [10, 20, None],
    'min_samples_split': [2, 5],
    'min_samples_leaf': [1, 2]
}
grid_search = GridSearchCV(RandomForestClassifier(random_state=42), param_grid, cv=5, n_jobs=-1)
grid_search.fit(X_train, y_train)
print(f"En iyi parametreler: {grid_search.best_params_}")

# Logistic Regression Modeli
scaler_standard = StandardScaler()
X_scaled = scaler_standard.fit_transform(X)  # Veriyi standartlaştırıyoruz
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.3, random_state=42)

model_lr = LogisticRegression(max_iter=2000, solver='liblinear', tol=1e-4)
model_lr.fit(X_train, y_train)
y_pred_lr = model_lr.predict(X_test)
accuracy_lr = accuracy_score(y_test, y_pred_lr)
print(f"Logistic Regression doğruluk oranı: {accuracy_lr * 100:.2f}%")
