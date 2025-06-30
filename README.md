# TimeCapsule

## Project Description

TimeCapsule is an Android application that allows users to create and send digital "time capsules" to other users. These capsules contain a message and are scheduled to be readable at a specific date and time in the future.

The application features user authentication (sign-up, sign-in, sign-out) managed via Firebase, and the core functionality of creating and managing these time capsules.

## Technologies Used

*   **Kotlin:** The primary programming language for Android development.
*   **Jetpack Compose & Material 3:** Android's modern declarative UI toolkit. This project leverages Compose for building its entire user interface, offering significant advantages over the traditional XML-based approach (see "Advantages of Jetpack Compose & Material 3" section below).
    *   Key Composables used: `Scaffold`, `TopAppBar`, `Column`, `Row`, `OutlinedTextField`, `Button`, `ExposedDropdownMenuBox`, `Icon`, `Text`, `IconButton`, `DropdownMenuItem`.
    *   **(Potentially `LazyColumn`/`LazyRow`):** For efficiently displaying lists of items, such as a list of created time capsules (assumed to be used in other parts of the app).
*   **Android Jetpack (Core Libraries):**
    *   **ViewModel:** Used to store and manage UI-related data in a lifecycle-conscious way (e.g., `CapsuleViewModel`, `AuthViewModel`).
    *   **StateFlow & `collectAsState()`:** To expose and observe UI states reactively.
    *   **`LaunchedEffect`:** For running side-effects tied to the lifecycle of a composable.
    *   **Navigation Component (implied by `NavController`):** For navigating between different screens of the app.
*   **Firebase:**
    *   **Firebase Authentication:** To manage user sign-up, sign-in, and sessions.
    *   **Firebase Realtime Database:** (Inferred) To store user information and the created time capsules.
*   **Coroutines:** For managing asynchronous tasks, such as network calls to Firebase and UI updates.
*   **Java Time API (`java.time`):** For handling dates and times (e.g., `LocalDate`, `LocalTime`, `LocalDateTime`, `Instant`).
*   **Android Patterns:**
    *   **MVVM (Model-View-ViewModel):** For structuring the application with a clear separation of concerns.

## Setting Up Firebase to Run the Project

**This project uses Firebase for backend services (Authentication, Database). To run this project locally, you will need to set up your own Firebase project and connect it to this application.**

1.  **Create a Firebase Project:**
    *   Go to the [Firebase Console](https://console.firebase.google.com/).
    *   Click on "Add project" and follow the on-screen instructions to create a new Firebase project.

2.  **Register your Android App with Firebase:**
    *   Inside your new Firebase project, click on the Android icon (</>) to add an Android app.
    *   You will be asked for the "Android package name". You can find this in your project's `app/build.gradle` file (look for the `applicationId` value, e.g., `com.example.timecapsule`).
    *   You can optionally provide a "App nickname" and a "Debug signing certificate SHA-1". The SHA-1 certificate is needed for features like Google Sign-In or Firebase Dynamic Links, but may not be immediately necessary just to get Authentication and Realtime Database/Firestore running for development. You can add it later if needed.
    *   Click "Register app".

3.  **Download `google-services.json`:**
    *   After registering the app, Firebase will provide you with a `google-services.json` configuration file. Download this file.

4.  **Add `google-services.json` to your Project:**
    *   Switch to the **Project** view in Android Studio (instead of the default Android view).
    *   Copy the downloaded `google-services.json` file and paste it into your project's `app` directory (i.e., `YourProjectName/app/google-services.json`).

5.  **Enable Firebase Services:**
    *   In the Firebase console, navigate to the services you need for this project:
        *   **Authentication:** Go to the "Authentication" section, click "Get started", and enable the "Email/Password" sign-in method (and any other methods you plan to use).
        *   **Realtime Database or Firestore:**
            *   If using **Realtime Database**: Go to the "Realtime Database" section, click "Create Database", and choose your location and security rules (start in "test mode" for development, but remember to secure your rules before production).
            *   If using **Cloud Firestore**: Go to the "Cloud Firestore" section, click "Create database", choose your location and security rules (start in "test mode" for development, but remember to secure your rules before production).
            *(This project infers the use of one of these. You'll need to set up the one the project is actually using).*

6.  **Sync your Project:**
    *   Back in Android Studio, sync your project with the Gradle files. Android Studio should automatically detect the `google-services.json` file.

After these steps, you should be able to build and run the application using your own Firebase backend.

## Advantages of Jetpack Compose & Material 3 (Over XML)

This project utilizes Jetpack Compose with Material 3 for its UI development, bringing several key benefits compared to the traditional Android View system (XML layouts):

1.  **Less Code & Increased Productivity:**
    *   Compose allows you to build UIs with significantly less boilerplate code. What might take many lines of XML and corresponding Kotlin/Java code can often be expressed more concisely in Compose.
    *   This leads to faster development cycles, easier maintenance, and fewer chances for bugs.
2.  **Declarative UI:**
    *   You describe *what* your UI should look like for a given state, and Compose takes care of *how* to update it when the state changes. This is a more intuitive approach compared to manually manipulating View hierarchies in the imperative XML world.
3.  **Intuitive Kotlin APIs:**
    *   Being entirely in Kotlin, Compose allows for powerful features like DSLs (Domain Specific Languages), functions as first-class citizens, and seamless integration with coroutines for asynchronous operations affecting the UI.
4.  **Powerful Tooling:**
    *   Android Studio provides excellent support for Compose, including interactive previews that update in real-time as you code, live edit of literals, and animation previews. This accelerates the design and iteration process.
5.  **Direct Kotlin Integration:**
    *   No more context switching between Kotlin/Java for logic and XML for layout. Your entire UI and its logic are in Kotlin, leading to a more unified and streamlined development experience.
6.  **Reusable Composables:**
    *   Building small, reusable UI components (Composables) is straightforward and encouraged, leading to a more modular and maintainable codebase.
7.  **Material 3 Integration:**
    *   Material 3 is the latest evolution of Google's design system, offering:
        *   **Dynamic Color:** UI colors can adapt to the user's wallpaper and theme, creating a more personalized experience.
        *   **Updated Components:** Modernized Material Design components with new styling and capabilities.
        *   **Design Tokens:** A more systematic way to manage styling attributes.
    *   Compose makes it easy to implement Material 3 guidelines and components.
8.  **Improved Testability:**
    *   The declarative nature and the ability to create isolated composables can simplify UI testing.

## Features

*   **User Authentication:**
    *   **Sign Up:** Allows new users to create an account.
    *   **Sign In:** Allows existing users to access their accounts.
    *   **Sign Out:** Allows authenticated users to end their session.
*   **Time Capsule Creation:**
    *   **Recipient Selection:** Users can select a recipient for their time capsule from a list of registered users (displayed in a dropdown).
    *   **Date & Time Picking:** Users can choose a specific date and time in the future when the capsule will become readable.
        *   Custom date picker modal.
        *   Custom time picker.
    *   **Message Input:** Users can write a message (up to 500 characters) to be included in the capsule.
    *   **Real-time Character Count:** Displays the current length of the message.
    *   **Capsule Saving:** Saves the created capsule (recipient, message, readable-at timestamp) to Firebase.
*   **Data Management & UI:**
    *   **State Management:** UI state (e.g., selected recipient, date, time, message content) is managed within ViewModels and observed by Compose UI.
    *   **Input Validation:** Ensures that necessary fields (recipient, message) are filled before allowing the capsule to be saved.
    *   **User Feedback:** Provides feedback to the user (e.g., Toast messages) on successful creation or errors.
*   **(Potentially) Listing Time Capsules:**
    *   The app likely has a screen where users can view a list of time capsules they've created or received. This would typically be implemented using `LazyColumn` or `LazyRow` for efficient display.

## How to Contribute

If you'd like to contribute to TimeCapsule, please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature/your-feature-name`).
6. Open a Pull Request.

Please make sure to update tests as appropriate.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details (if you choose to add one).
