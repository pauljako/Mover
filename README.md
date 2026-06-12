# Mover

### A Public Transit App for WearOS

## Overview

Mover is an Open-Source App for WearOS, Google's Watch Operating System.
It allows you to see the Departures of Public Transit Stops around the World thanks to [Transitous](https://transitous.org).
Features such as Routing or [Träwelling](https://traewelling.de) Check-In may also be implemented.

## Current Feature-Set and ToDo

- [x] Search for Stops
    - [ ] Select Search Results
- [ ] Show nearby Stops
- [ ] Remember last Stops
- [x] Show departures of Stop
- [ ] Detailed Route View (See entire Journey, Map, etc.)

## Try it Out!

Currently, there are no Binary Releases available.
However, you can still compile it from Source.
Either open it in Android Studio or run the following command (after the Repo has been cloned):
`./gradlew assembleDebug`.

The APK should then be inside `app/build/outputs/apk/debug/app-debug.apk`.
You can [Sideload](https://xdaforums.com/t/official-list-of-sideloaded-apps-and-workarounds-for-wear-os-tested-on-galaxy-watch.4379825/)
it onto your Device.