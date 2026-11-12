#!/usr/bin/env bash
set -euo pipefail

apk_dir="${1:-app/build/ci-apks}"
reports="app/build/reports/device-tests"
screenshots="app/build/outputs/screenshots"
mkdir -p "$reports" "$screenshots"

collect_screenshots() {
  if adb shell test -d /sdcard/Android/data/com.sertumrosarum/files/screenshots; then
    adb pull /sdcard/Android/data/com.sertumrosarum/files/screenshots/. "$screenshots/" || true
  fi
}
collect_on_exit() {
  result=$?
  adb logcat -d > "$reports/logcat.txt" 2>&1 || true
  collect_screenshots
  exit "$result"
}
trap collect_on_exit EXIT

adb wait-for-device
# A warmed snapshot may contain a private-signed release from the previous run.
adb uninstall com.sertumrosarum.test > /dev/null 2>&1 || true
adb uninstall com.sertumrosarum > /dev/null 2>&1 || true
adb install -r "$apk_dir/app-debug.apk"
adb install -r -t "$apk_dir/app-debug-androidTest.apk"
adb shell pm clear com.sertumrosarum > /dev/null
adb shell input keyevent 82
adb shell am instrument -w -r com.sertumrosarum.test/androidx.test.runner.AndroidJUnitRunner | tee "$reports/instrumentation.txt"
# Android's runner may return exit code 0 even when a JUnit test fails.
grep -Eq '^OK \([1-9][0-9]* tests?\)' "$reports/instrumentation.txt"
# Pull while the test app is still installed; uninstalling removes its external files.
collect_screenshots
test "$(find "$screenshots" -name '*.png' | wc -l)" -ge 6

# Exercise the real minified release as well, catching R8 or signing regressions.
adb uninstall com.sertumrosarum
adb install -r "$apk_dir/app-release.apk"
adb shell am start -W -n com.sertumrosarum/.MainActivity | tee "$reports/release-launch.txt"
grep -q 'Status: ok' "$reports/release-launch.txt"
adb shell uiautomator dump /sdcard/sertum-release.xml
adb pull /sdcard/sertum-release.xml "$reports/release-window.xml"
python3 - "$reports/release-window.xml" <<'PY'
import sys
import xml.etree.ElementTree as ET
nodes = list(ET.parse(sys.argv[1]).iter("node"))
assert any(node.get("package") == "com.sertumrosarum" and node.get("text", "").casefold() == "sertum" for node in nodes), "The optimized release did not display the sanctuary"
print("Optimized release sanctuary launch passed")
PY
