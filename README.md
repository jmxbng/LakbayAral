# 🏛️ LAKBAY ARAL
### Exploring History Through Augmented Reality

A gamified Android AR app for exploring the historical heritage of Pampanga municipalities.

---

## 📋 PROJECT OVERVIEW

Lakbay Aral is a mobile game-style app where users:
- Explore Pampanga municipalities via AR
- Spawn 3D historical objects on real-world surfaces
- Tap objects to read holographic history panels
- Take quizzes to unlock the next municipality
- Compete on a global leaderboard

**Starting municipality:** Lubao, Pampanga  
**Progression:** Pass quiz (≥70%) → unlock next municipality

---

## 🛠️ TECH STACK

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| AR | ARCore 1.44 + SceneView 2.2 |
| DI | Hilt |
| Backend | Firebase Auth + Firestore |
| Local storage | SharedPreferences + Gson |
| Navigation | Compose Navigation |
| Architecture | MVVM + Repository |

---

## ⚙️ SETUP INSTRUCTIONS

### 1. Prerequisites
- Android Studio Hedgehog or newer
- Android device with ARCore support (API 26+)
- Google account for Firebase

### 2. Clone / Open Project
```bash
# Open in Android Studio
File → Open → select /LakbayAral folder
```

### 3. Firebase Setup (Required for leaderboard)

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project: `lakbay-aral`
3. Add Android app with package: `com.example.lakbayaral`
4. Download `google-services.json`
5. **Replace** `app/google-services.json` with your downloaded file
6. Enable **Firestore Database** in Firebase console
7. Enable **Anonymous Authentication** in Firebase console

Set Firestore rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /leaderboard/{uid} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

> **Note:** The app works fully offline without Firebase — leaderboard will show only your local score.

### 4. Add 3D Models (for AR objects)

Place `.glb` model files in:
```
app/src/main/assets/models/
```

Required models (from `LocalDataSource.kt`):
- `church.glb`     – St. Peter Parish Church
- `bridge.glb`     – Old Lubao Bridge
- `monument.glb`   – Lubao Town Plaza
- `bust_general.glb` – General Tinio hologram
- `bust_chief.glb`   – Don Macabulos hologram

**Free 3D model sources:**
- [Sketchfab](https://sketchfab.com) (search for Filipino heritage architecture)
- [Poly Pizza](https://poly.pizza)
- [Google Scans](https://arvr.google.com/intl/en_uk/ar/)

> The app will gracefully run without models — tap indicators and history panels still work.

### 5. Build & Run
```
Build → Make Project  (Ctrl+F9)
Run → Run 'app'       (Shift+F10)
```

---

## 📁 PROJECT STRUCTURE

```
com.example.lakbayaral/
├── LakbayAralApp.kt          ← Hilt Application class
├── MainActivity.kt           ← Entry point
│
├── ui/
│   ├── theme/Theme.kt        ← Dark cinematic color scheme
│   ├── navigation/NavHost.kt ← All routes
│   ├── components/           ← GlassCard, NeonButton, etc.
│   ├── splash/               ← Animated logo screen
│   ├── login/                ← Player name entry
│   ├── mainmenu/             ← Game-style main hub
│   ├── map/                  ← Municipality selector
│   ├── municipality/         ← Intro + AR mode selection
│   ├── armode/               ← AR camera + history panel
│   ├── quiz/                 ← Multi-choice quiz engine
│   └── leaderboard/          ← Top 10 global ranking
│
├── ar/
│   ├── ARController.kt       ← ARCore session config
│   ├── ARSpawner.kt          ← 3D model placement
│   └── ARInteractionManager.kt ← Tap gesture routing
│
├── data/
│   ├── models/Models.kt      ← Data classes
│   └── repository/
│       ├── LocalDataSource.kt    ← Historical content (Lubao)
│       ├── UserProgressRepository.kt ← Local save
│       └── FirebaseRepository.kt     ← Leaderboard
│
└── di/AppModule.kt           ← Hilt providers
```

---

## 🎮 GAME FLOW

```
Splash → Login → Main Menu → Map
                              ↓
                        Municipality (Lubao)
                         /           \
                   AR Places        AR People
                        \           /
                         Quiz (70%+)
                              ↓
                     Next municipality unlocks
```

---

## 📱 DEVICE REQUIREMENTS

- Android 8.0+ (API 26)
- ARCore-supported device ([full list](https://developers.google.com/ar/devices))
- Camera permission
- Internet (optional, for leaderboard)

---

## 🔧 EXTENDING THE APP

### Add a new Municipality
1. Open `LocalDataSource.kt`
2. Add a new `Municipality(...)` object with places, people, and quiz
3. Add it to the `municipalities` list
4. The map screen and unlock system handle the rest automatically

### Add more quiz questions
Edit the `quiz` list inside each `Municipality` object in `LocalDataSource.kt`.

### Customize AR behavior
- `ARSpawner.kt` — change model scale, animations, placement mode
- `ARInteractionManager.kt` — change spawn order logic

---

## 🎨 DESIGN SYSTEM

| Token | Value |
|-------|-------|
| Gold | `#C8A24A` |
| Dark BG | `#0B0F1A` |
| Neon Blue | `#4DE3FF` |
| White Glow | `#EAEAEA` |
| Style | Glassmorphism + dark cinematic |

---

## 📦 KEY DEPENDENCIES

```toml
arcore     = "1.44.0"
sceneview  = "2.2.1"    # Modern ARCore + Filament renderer
compose-bom = "2024.08.00"
firebase-bom = "33.2.0"
hilt        = "2.51.1"
```

---

## 📝 NOTES

- SceneView 2.x is the modern replacement for the deprecated Sceneform SDK
- All quiz results and unlock progress persist locally via SharedPreferences
- The leaderboard degrades gracefully when offline
- AR objects spawn sequentially — one per tap on a detected plane

---

*Lakbay Aral · Pampanga Heritage Series · v1.0*
