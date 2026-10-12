# My Voice AI (starter Android project)

This starter Android app listens to Hindi speech, displays recognized words, and can speak the displayed text aloud. It is not a full online ChatGPT-style assistant yet.

## Build APK with GitHub Actions (phone-friendly)
1. Create a GitHub account if needed.
2. Create a new repository named `MyVoiceAI`.
3. Upload the files and folders from this ZIP into the repository (keep the `.github/workflows/build-apk.yml` file).
4. Open the repository's **Actions** tab and choose **Build My Voice AI APK**.
5. Tap **Run workflow**. Wait for the workflow to finish.
6. Open the completed run, scroll to **Artifacts**, and download `MyVoiceAI-debug-apk`.
7. Extract the downloaded ZIP and install `app-debug.apk`. Android may ask you to allow installs from that source.

## Notes
- This project uses Android's built-in speech recognition and Text-to-Speech.
- First launch asks for microphone permission.
- Speech recognition may use Google's speech services and internet depending on device settings.
- The APK is unsigned for Play Store release, but is suitable for personal testing.
