# 💬 ChatApp: Native Android Real-Time Suite

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00.svg?logo=java)](https://www.java.com/)
[![Backend](https://img.shields.io/badge/Backend-Firebase-FFCA28.svg?logo=firebase)](https://firebase.google.com/)
[![Version](https://img.shields.io/badge/Version-1.0.0-gold.svg)](#)

A high-performance, native Android messaging platform engineered over **6 months** of intensive development. This application leverages the power of **Java** and **Firebase** to provide a seamless, secure, and low-latency communication experience.

---

## 📖 Project Overview

**ChatApp** is a robust mobile solution designed for instant connectivity. Developed using **Android Studio**, it utilizes a real-time NoSQL architecture to ensure that messages, media, and user states are synchronized instantly across all devices.

### 🛠 The 6-Month Engineering Journey:
* **UI/UX Refinement:** Dozens of iterations in XML to achieve a fluid "Glassmorphism" design.
* **Database Optimization:** Structured Firebase Realtime Database/Firestore for high-speed retrieval.
* **Complex Logic:** Implementation of custom Adapters, Fragments, and Service listeners for background sync.

---

## 🚀 Key Features

* **Instant Messaging:** Real-time text delivery powered by Firebase Realtime Database.
* **Firebase Authentication:** Secure login/signup using Email and Google OAuth.
* **Multimedia Sharing:** Send images and documents via Firebase Storage integration.
* **Live Presence System:** Real-time "Online" status tracking and typing indicators.
* **Push Notifications:** Integrated **Firebase Cloud Messaging (FCM)** for instant alerts.
* **User Search & Discovery:** Find friends instantly using an optimized search query logic.
* **Profile Management:** Custom avatars and bio updates with real-time reflect.

---

## 📸 Output & Visuals

The app features a modern, clean interface built entirely with custom XML layouts.

| Login/Auth Screen | Chat Interface |
|:---:|:---:|
| <img src="./screenshots/auth_mobile.png" width="250" alt="Login View" /> | <img src="./screenshots/chat_mobile.png" width="250" alt="Chat View" /> |

> **Note:** Screenshots are located in the `/screenshots` directory.

---

## 🛠️ Technical Stack & Architecture

Built with a **Model-View-Controller (MVC)** pattern for clear separation of concerns.

| Component | Technology | Description |
|:--- |:--- |:--- |
| **Language** | Java (JDK 11+) | Core application logic and multi-threading. |
| **UI Design** | XML / Material Design | Responsive layouts and custom animations. |
| **Backend** | Firebase Realtime DB | JSON-based cloud database for live sync. |
| **Authentication** | Firebase Auth | Secure identity management and session handling. |
| **Media Hosting** | Firebase Storage | Scalable storage for shared user content. |
| **Networking** | Retrofit / Volley | API integration for external data services. |

---

## ⚙️ Installation & Setup

To run this project in **Android Studio**:

1. **Clone the Project**
   ```bash
   git clone [https://github.com/sanjithms/chatapp.git](https://github.com/sanjithms/chatapp.git)
