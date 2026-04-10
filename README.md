<div align="center">

# 💬 MtoM — Secure Messenger

<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
<img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose"/>
<img src="https://img.shields.io/badge/Firebase-DD2C00?style=for-the-badge&logo=firebase&logoColor=white" alt="Firebase"/>
<img src="https://img.shields.io/badge/WebRTC-333333?style=for-the-badge&logo=webrtc&logoColor=white" alt="WebRTC"/>

<br/><br/>

> 🔒 End-to-end encrypted messaging & voice calling app built with modern Android stack

<br/>

![Min SDK](https://img.shields.io/badge/Min_SDK-23-blue?style=flat-square)
![Target SDK](https://img.shields.io/badge/Target_SDK-34-blue?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0.1-green?style=flat-square)
![License](https://img.shields.io/badge/License-Private-red?style=flat-square)

</div>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🔐 **E2EE Messaging** | AES-256-GCM encryption with unique keys per conversation |
| 📞 **Voice Calls** | Real-time audio calls powered by WebRTC |
| 💬 **Real-time Chat** | Instant messaging via Firebase Realtime Database |
| 🌙 **Dark Mode** | Full monochrome dark/light theme support |
| 🔔 **Smart Notifications** | MessagingStyle stacked notifications with inline reply |
| 🟢 **Online Status** | Live online/offline presence with lifecycle awareness |
| 💾 **Chat Backup** | Local & Google Drive backup/restore |
| 🔑 **Secure Auth** | Phone-based OTP authentication with SHA-256 tokens |

---

## 🏗️ Architecture

```
📦 com.fahimshahriarv1.mtom
├── 📂 data
│   ├── 🔒 crypto          # E2EE encryption (AES-256-GCM)
│   ├── 🔥 firebase         # Firestore, RTDB, Signaling
│   ├── 💾 repository       # Repository implementations
│   ├── 🗄️ room             # Local database (Room)
│   └── 📡 webrtc           # WebRTC audio client
├── 📂 di                   # Hilt dependency injection
├── 📂 domain
│   ├── 📋 model            # Domain models
│   ├── 📦 repository       # Repository interfaces
│   └── ⚙️ usecases         # Business logic
├── 📂 presentation
│   ├── 🧭 navgraph         # Navigation (Compose Nav)
│   └── 🎨 ui               # Screens & ViewModels
└── 📂 service              # Foreground services
```

---

## 🔒 End-to-End Encryption

Messages and timestamps are encrypted **on-device** before leaving the app. The server only sees ciphertext.

```
🔑 Key Derivation
┌─────────────────────────────────────────┐
│  SHA-256( user1 : user2 : asciiSum :    │
│           SECRET_CONSTANT )             │
│         ↓                               │
│  Unique 256-bit AES key per chat        │
└─────────────────────────────────────────┘

📤 Sending                    📥 Receiving
┌──────────┐                  ┌──────────┐
│ plaintext│─→ AES-GCM ─→     │ciphertext│
│+timestamp│   encrypt   │    │          │─→ AES-GCM ─→ plaintext
└──────────┘             │    └──────────┘   decrypt     +timestamp
                    Firebase RTDB
                    (relay only)
```

- 🔑 **Unique key** per conversation — derived from both usernames
- 🛡️ **AES-256-GCM** — authenticated encryption with random IV per message
- 📱 **Device-only** — keys never leave the device, never stored on server
- ⏱️ **Timestamps included** — encrypted alongside message content

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| 🎨 **UI** | Jetpack Compose + Material 3 |
| 🏛️ **Architecture** | MVVM + Clean Architecture |
| 💉 **DI** | Dagger Hilt |
| 🗄️ **Local DB** | Room |
| ☁️ **Backend** | Firebase (Firestore + RTDB + Auth + Crashlytics) |
| 🔐 **Encryption** | AES-256-GCM + SHA-256 |
| 🧭 **Navigation** | Compose Navigation |
| 💾 **Backup** | Google Drive API |

---

## ⚡ Getting Started

### Prerequisites

- 🟢 Android Studio Hedgehog or later
- 🟢 JDK 17
- 🟢 Firebase project with Firestore, RTDB, and Auth enabled

### Setup

1️⃣ **Clone the repo**
```bash
git clone https://github.com/fahimshahriarv1/MtoM.git
```

2️⃣ **Add Firebase config**
```
Place your google-services.json in app/
```

3️⃣ **Add encryption secret** to `local.properties`
```properties
E2EE_SECRET=your_secret_here
```

4️⃣ **Firebase RTDB Rules**
```json
{
  "rules": {
    "registered_users": { ".read": true, ".write": true },
    "messages": {
      "$recipientId": {
        ".read": "root.child('registered_users').child($recipientId).exists()",
        "$messageId": {
          ".write": "!newData.exists() || root.child('registered_users').child($recipientId).exists()"
        }
      }
    },
    "_test_ping": { ".read": true, ".write": true }
  }
}
```

5️⃣ **Build & Run** 🚀
```bash
./gradlew assembleDebug
```

---

## 📱 Screenshots

> _Coming soon_

---

## 📄 License

This project is private and not open for public use.

---

<div align="center">

**Built with ❤️ using Kotlin & Jetpack Compose**

<img src="https://img.shields.io/badge/Made_with-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>

</div>
