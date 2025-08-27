# 🚀 Space Invaders: Boss Edition  

A modern **Java + Gradle** powered reimagining of the classic *Space Invaders* arcade game, featuring:  

- 👾 Classic enemy waves  
- 💀 A powerful **Boss Enemy** every 5th wave  
- 💥 **Cluster Bomb projectiles** that split mid-air  
- 🎵 Sound effects & background music  
- 🕹️ Smooth gameplay with responsive controls  
- 🔥 Bonus timer & score multipliers  

---

## 🛠️ Tech Stack  
- **Java (Swing)** – Core game logic & rendering  
- **Gradle** – Build & dependency management  
- **OOP Architecture** – Clean separation of Player, Enemy, Boss, and Projectiles  
- **SpriteLoader** – Efficient texture/sprite management  
- **SoundPlayer** – Custom sound handling system  

---

## 🎮 Features  
- Player movement & shooting system  
- Multiple enemy ship types  
- Boss spawns with **3 lives** and unique attack patterns  
- **Cluster Bomb attack**: Boss fires a large projectile that explodes into splinter shots  
- **Floating Text** for feedback (damage, bonus, etc.)  
- Score & bonus timer system  

---

## ▶️ How to Run  
1. Clone the repository:  
   ```bash
   git clone https://github.com/your-username/space-invaders-boss.git
   cd space-invaders-boss
   ```  

2. Build & run with Gradle:  
   ```bash
   ./gradlew run
   ```  

3. Enjoy blasting aliens! 👽  

---

## 🎹 Controls  
- **Arrow Keys** – Move left/right  
- **Space** – Shoot  
- **ESC** – Pause/Quit  

---

## 🗂️ Project Structure  
```
src/main/java/
 ├── Main.java              # Entry point
 ├── GameWindow.java        # Main game window
 ├── GamePanel.java         # Rendering & game loop
 ├── Player.java            # Player logic
 ├── EnemyShip.java         # Basic enemy logic
 ├── BossEnemy.java         # Boss AI & attack patterns
 ├── Projectile.java        # Base projectile
 ├── BossProjectile.java    # Boss projectile logic
 ├── SplinterProjectile.java# Splitting mini projectiles
 ├── FloatingText.java      # Text effects
 ├── SoundPlayer.java       # Audio system
 └── SpriteLoader.java      # Image/sprite loader
```  

---

## 🌟 Future Plans  
- 🎨 Custom sprite skins  
- 🧑‍🤝‍🧑 Local multiplayer  
- 🌍 Online leaderboard  
- ⚡ Power-ups and new weapons  

---

## 📜 License  
This project is released under the **MIT License**.  

---
✨ Built with ❤️ by Luca Accomando
