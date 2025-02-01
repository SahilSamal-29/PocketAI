# PocketAI
Below is a professional README in Markdown format without extraneous code:

---

# PocketAI

**PocketAI** is an Android application that demonstrates efficient on-device inference using a lightweight large language model (LLM). The app leverages the quantized Gemma 2B model to generate text responses directly on the device, ensuring low latency and preserving user privacy. The user interface is built with Jetpack Compose for a modern, responsive chat experience.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Inference Model](#inference-model)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Overview

PocketAI runs the Gemma 2B LLM locally on Android devices. The model is quantized (using INT4 precision) to optimize memory usage and inference speed. The app includes a responsive chat interface that streams partial responses in real time, providing a smooth user experience even on mid-range devices.

---

## Features

- **On-Device Inference:**  
  The application runs the Gemma 2B model entirely on-device, ensuring quick responses and enhanced data privacy.

- **Quantization Optimizations:**  
  The quantized model reduces memory footprint and speeds up inference without sacrificing quality.

- **Responsive Chat UI:**  
  Built with Jetpack Compose, the chat interface supports real-time streaming of responses, dark/light mode, and dynamic settings adjustments.

- **Configurable Parameters:**  
  Users can adjust inference parameters such as maximum tokens and top-K values, which dynamically reinitialize the inference engine.

- **Thread-Safe Operations:**  
  Uses Kotlin coroutines and synchronization mechanisms to manage asynchronous inference, preventing concurrency issues.

---

## Architecture

The application is designed with a clear separation between the UI and inference layers:

- **User Interface:**  
  Developed using Jetpack Compose for a modern, fluid chat experience.

- **Inference Engine:**  
  A custom inference engine encapsulated in the `InferenceModel` class handles model initialization, parameter management, and asynchronous text generation.

- **Persistence:**  
  Model parameters are stored and managed using Android's SharedPreferences, allowing for dynamic configuration.

---

## Inference Model

The core of the on-device inference is implemented in the `InferenceModel` class. This class:

- **Initializes the Quantized Model:**  
  Verifies the presence of the model file at a specified path and creates the inference engine with default parameters.

- **Manages Inference Parameters:**  
  Provides methods to update parameters such as maximum tokens and top-K values. Any changes trigger a safe reinitialization of the model.

- **Streams Partial Results:**  
  Uses Kotlin’s shared flow mechanism to emit partial results, enabling real-time UI updates as text is generated.

- **Ensures Thread-Safe Execution:**  
  Implements a mutex and uses coroutines to handle asynchronous inference calls without race conditions.

---

## Installation & Setup

### Prerequisites

- **Android Studio:** Latest version recommended.
- **Device/Emulator:** Running Android 5.0 (Lollipop) or higher.
- **Git:** For cloning the repository.

### Steps

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/SahilSamal-29/PocketAI.git
   cd PocketAI
   ```

2. **Open the Project:**
   - Launch Android Studio and open the `PocketAI` project directory.

3. **Add the Model File:**
   - Make sure that Gemma model is downloaded in your device. If not you can download it from here.(https://www.kaggle.com/models/google/gemma/tfLite/gemma-2b-it-cpu-int4)
   - Ensure that the quantized model file (`gemma-2b-it-cpu-int4.bin`) is available at `/data/local/tmp/` on your device or update the model path in the code accordingly.

5. **Build and Run:**
   - Sync Gradle, build the project, and run it on your device or emulator.

---

## Usage

- **Chat Interface:**  
  Launch the app to access a modern chat interface where you can input text prompts.

- **Real-Time Response Streaming:**  
  The app streams partial results from the model, offering immediate feedback as the response is generated.

- **Dynamic Parameter Adjustment:**  
  If implemented, the settings panel allows users to adjust parameters like maximum tokens and top-K values, dynamically reconfiguring the inference engine.

---

## Project Structure

```
PocketAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                    # Application source code including InferenceModel.kt
│   │   │   ├── res/                     # UI resources (layouts, themes, etc.)
│   │   │   └── assets/                  # Additional assets (if needed)
│   └── build.gradle
├── build.gradle
└── README.md
```

---
## Screenshots

### Splash Screen
<img src="https://github.com/user-attachments/assets/f2b45520-e293-467e-872f-947759bc9b60" alt="Screenshot" width="250">
<img src="https://github.com/user-attachments/assets/f097437c-7769-4f77-84e1-131dfb4585c4" alt="Screenshot" width="250">
<br>
[here's a short demonstration of PocketAI](https://youtube.com/shorts/T1WK3Idxc24?feature=share)

## Contributing

Contributions are welcome. If you have suggestions, bug fixes, or enhancements, please open an issue or submit a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

For questions or feedback, please contact:

**Sahil Samal**  
Email: [your.email@example.com](samalsahil29@gmail.com)
Contact no: 9833766076
