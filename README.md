# Hajri — 1/31 Days

Attendance cards for daily-wage workers. Native Android app, offline-first:
all data lives on the phone as local JSON, no network, no account.

## What it does

- **Home** — one card per worker per month; **Card detail** — tap a day to
  mark it; **Total** — days × rate − withdrawal = arrears.
- Work tap cycles blank → ✓ → ✗ → ½ (½ = half day). OT tap toggles ✓;
  2 OT ticks = 1 work day.
- **PIN lock** (changeable in Settings) guards changing a marked day,
  editing and deleting.
- **₹49 once unlocks everything for lifetime on that device**: PDF export,
  Payment Screenshot, Reminders, Remove Ads. Pay by UPI (QR in the app),
  send the 12-digit UTR, get a 9-digit code — the app checks its SHA-256 hash.
- **Reminders** — daily notification at a chosen time (default 21:00) when
  today is still unmarked (Premium).
- **Banner ad** at the bottom of Home while locked (AdMob).

## Themes

Four themes, rewritten from scratch in v2.1: Glass (frosted translucent
over a dark sapphire-teal gradient), Material You (live Android 12+ wallpaper
colours), Dark, Day.

## Build

Open in Android Studio (minSdk 26, targetSdk 33, compileSdk 37) or build
with Gradle. No network is used at all.
