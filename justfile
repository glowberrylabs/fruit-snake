export JAVA_HOME := env("JAVA_HOME", `/usr/libexec/java_home -v 21 2>/dev/null || echo /usr/lib/jvm/java-21-openjdk`)

avd := "pixel9a"
app_id := "com.glowberrylabs.fruitsnake"

# List available recipes
default:
    @just --list

# Build the debug APK
build:
    bash gradlew assembleDebug

# Build and install to connected device
install: build
    adb install -r app/build/outputs/apk/debug/app-debug.apk

# Build, install, and launch the app
run: install
    adb shell am start -n {{ app_id }}/.MainActivity

# Start the emulator and wait until it is ready
emu:
    emulator -avd {{ avd }} &
    adb wait-for-device
    adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
    adb shell cmd overlay enable com.android.internal.systemui.navbar.threebutton

# Shut down the emulator
emu-kill:
    adb -s emulator-5554 emu kill

# Stream logs from the running app
logcat:
    adb logcat --pid=$(adb shell pidof {{ app_id }})

# Regenerate the bundled sound effects
sounds:
    python3 scripts/make_sounds.py

# Run unit tests
test:
    bash gradlew test

# Check formatting and lint (does not modify files)
check:
    bash gradlew ktlintCheck detekt

# Auto-format code
fmt:
    bash gradlew ktlintFormat

# Clean build artifacts
clean:
    bash gradlew clean
