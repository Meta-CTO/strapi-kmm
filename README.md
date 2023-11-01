# Strapi KMM Library

The Strapi KMM Library is a Kotlin Multiplatform Mobile library that provides a unified interface
for interacting with Strapi APIs across iOS, Android, and JavaScript web applications. It offers a
set of functionalities for handling authentication, making HTTP requests, processing deep links, and
more.

## Features

- **Multiplatform Support**: Write code once and use it across iOS, Android, and JavaScript web
  applications.

- **Strapi API Integration**: Easily connect your mobile and web applications with Strapi APIs.

- **Authentication**: Securely handle user authentication with various providers, including Apple
  and Google using Firebase.

- **HTTP Requests**: Make HTTP requests to Strapi endpoints with a simplified API using Ktor HTTP
  Client and HTTP extensions.

- **Paging Support**: Manage paginated responses from Strapi APIs with ease.

- **Deep Link Processing**: Process deep links using Firebase Dynamic Links.

- **Analytics**: Track user events and send them to Amplitude and CleverTap.

- **Background Downloads**: Handle background downloads for files in iOS and Android.

- **Contacts data collector**: Collect contacts data from the user's device in iOS and Android.

- **Files Uploader**: Upload files to AWS S3 and Strapi Media Library.

- **Date Formatting**: Format dates according to specified formats.

## Getting Started

### Installation

You can add the Strapi KMM Library to your kmm project using using Github packages:

**iOS (SwiftUI / UIKit)**:

Add the library to your Xcode project using Swift Package Manager. Provide the library's repository
URL as the package source.

```kotlin
implementation("com.swensonhe:Strapi:8.0.1")
```

### Usage

#### Authentication

The authentication process in the Strapi KMM Library is facilitated by the `AuthRepository` class.
It offers a range of functions for seamless user authentication across different platforms. We
recommend using
the [@swensonhe/strapi-plugin-firebase-auth](https://github.com/swensonhe/strapi-firebase-auth)
plugin to simplify authentication on both the client and server sides.

##### Initialization

To start using the authentication features, initialize the `AuthRepository` with the required
dependencies:

```kotlin
val authRepository =
    AuthRepository(authService, userRepository, sharedPreference, actionCodeSettings)
```

userRepository is an instance of the `UserRepository` class, which is used to fetch the user data.
sharedPreference is an instance of the `SharedPreferences` class, which is used to store user
tokens. actionCodeSettings is an instance of the Firebase `ActionCodeSettings` class, which is used
to configure the behavior of the email link sent to the user.

#### Example: Sign In with Google

```kotlin
// The [AuthOptions] class provides the configuration options for the [AuthClient] which is required for Android.
try {
    val user = authRepository.signInWithGoogle<User>(authOptions)
    // Handle successful sign-in
} catch (throwable: Throwable) {
    // Handle authentication failure
}
```

#### Example: Sign In with Apple (iOS & Web)

```kotlin
try {
    val user = authRepository.signInWithApple<User>()
    // Handle successful sign-in
} catch (e: Throwable) {
    // Handle authentication failure
}
```

#### Sending and Verifying Email Links

The library also supports sending and verifying email links for authentication. You can send a
sign-in link to the user's email address, and later, the user can sign in using the received link.

#### Sign Out

To log the user out, clear the cached user data, remove the access token, and sign out the user from
Firebase.

```kotlin
authRepository.signOut()
```

For more details on how the Strapi KMM Library handles authentication, refer to the source code of
the `AuthRepository` class.

Remember to use the recommended Firebase authentication plugin for a more streamlined authentication
process.

-----

#### Tracking

The `AnalyticsManager` in the Strapi KMM Library simplifies event tracking by allowing you to manage multiple analytics services seamlessly. It supports services like Amplitude and CleverTap.

###### User Properties

You can set user properties across all configured analytics services, providing user-specific information.

```kotlin
analyticsManager.setUserProperties(userId, userEmail, userPhone, extraProperties)
```

##### Logout
Logging out the user from all analytics services is as straightforward as calling:

```kotlin
analyticsManager.logout()
```

##### Tracking Events

The library makes it easy to track events. You can track basic events or events with additional properties on all configured analytics services.

```kotlin
analyticsManager.trackEvent(eventName)
analyticsManager.trackEvent(eventName, eventProperties)
```

For more custom tracking, use the `trackEvent` function, providing a `TrackingEvent` instance.

##### Registering Services

Registering analytics services with the `AnalyticsManager` is a breeze using the builder pattern:

```kotlin
val analyticsManager = AnalyticsManager.Builder(context)
    .setAmplitudeService(amplitudeKey)
    .setCleverTapAnalyticsService()
    .build()
```

The library handles service initialization and registration, streamlining the analytics setup process.

By using the `AnalyticsManager` in the Strapi KMM Library, you can efficiently manage and track events on multiple analytics services in your KMM project.

-----

#### Background Downloader

The `BackgroundDownloader` is an essential component in the Strapi KMM Library, designed to facilitate asynchronous content downloading from URLs. It abstracts the downloading process and allows you to handle ongoing downloads efficiently.

##### Key Features

- **Download Status Listener:** The downloader provides a listener that tracks the progress of ongoing downloads. You can monitor the status of your downloads in real-time.

- **Concurrent Downloads:** You can configure the maximum number of concurrent downloads to ensure optimal performance while downloading content.

- **Cellular Network Permissions:** It offers flexibility by allowing you to specify whether cellular network connections are permitted for downloads, optimizing your data usage.

##### Functions

- `download(url: String)`: Asynchronously downloads content from the specified URL. It returns a unique process ID for tracking the download status.

- `download(urls: List<String>)`: Downloads content from a list of URLs asynchronously. You receive a list of process IDs, one for each download, allowing you to monitor multiple downloads simultaneously.

- `resumeUnfinishedDownloads()`: This function is useful for resuming any unfinished downloads that were interrupted or paused, ensuring seamless content retrieval.

##### Platform Compatibility

Integrate the `BackgroundDownloader` into your KMM project for efficient background content downloading and management, it


## Contributing

We welcome contributions to the Strapi KMM Library! If you find a bug or have a feature request,
please [open an issue](link-to-issues). We appreciate your input.

If you'd like to contribute code, please follow
our [contribution guidelines](link-to-contribution-guidelines).

## License

This library is open-source and available under the [MIT License](link-to-license).

---

Feel free to add more details and tailor the README to your specific library and project
requirements. Don't forget to include sections like "Contributing" and "License" that provide
information on how others can contribute to your project and the terms of use.