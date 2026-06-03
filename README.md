# BESS Intelligent SCADA Prototype (1.25MW / 1270V)

![Status](https://img.shields.io/badge/Status-Live-green)
![Language](https://img.shields.io/badge/Language-Java%2017-orange)
![Tech](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)

A professional SCADA prototype for utility-scale Battery Energy Storage Systems. This system features a high-fidelity Java physics engine and a real-time web dashboard.

---

## 📊 Live Demo
If deployed on Render, your link will be: `https://YOUR-APP-NAME.onrender.com`

---

## 🚀 Key Features

### 1. Advanced Physical Modeling
- **High-Voltage Simulation**: 1270V DC nominal bus voltage.
- **Polarization Effect**: Real-time internal resistance ($0.2\Omega$) modeling.
- **Asymmetric Current**: Accurately simulates physics where discharge current magnitude exceeds charge current due to voltage drops.

### 2. Industrial Control Logic
- **Safety Interlock**: Power commands are inhibited until the Grid Breaker is "CLOSED".
- **Operation Audit**: Email-based authentication for critical switch operations with real-time logs.
- **Priority Protection**: Emergency Stop and Breaker Open commands have immediate override priority.

### 3. Professional Telemetry
- **Multi-Axis Charts**: Real-time trending for SOC, Grid Frequency, and Signed Current.
- **Dynamic SLD**: SVG-based animated Single Line Diagram.

---

## 🛠️ Deployment Instructions

### Cloud Deployment (Render.com)
1. Link this GitHub repo to **Render.com**.
2. Select **Docker** as the runtime.
3. Add environment variable `PORT = 8080`.
4. System will be live automatically.

### Local Development
1. Clone the repo.
2. Run `mvn clean package`.
3. Start `ScadaApplication.java`.
4. Access `http://localhost:8080`.

---

## ✉️ Author
**Target Position**: Delivery Integration Engineer
**Project Focus**: Industrial OT/IT Integration, BESS Control Systems, Java Backend Engineering.
