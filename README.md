# Mover

### A Public Transit App for WearOS

#### [Video Demo](https://vimeo.com/1203467818)

## Overview

Mover is an Open-Source App for WearOS, Google's Watch Operating System.
It allows you to see the Departures of Public Transit Stops around the World thanks
to [Transitous](https://transitous.org).
Features such as Routing or [Träwelling](https://traewelling.de) Check-In may also be implemented.

## Current Feature-Set and ToDo

- [x] Search for Stops
    - [x] Select Search Results
- [x] Show nearby Stops
- [x] Remember last Stops
- [x] Show departures of Stop
- [ ] Detailed Route View (See entire Journey, Map, etc.)

## Try it Out!

Binary Releases are either available on the
[GitHub Releases](https://github.com/pauljako/Mover/releases/latest) or on
my [WearDroid F-Droid Repository](https://files.pauljako.de/WearDroid/fdroid/repo)!

Additionally, you can compile it from Source.
Either open it in Android Studio or run the following command (after the Repo has been cloned):
`./gradlew assembleDebug`.

The APK should then be inside `app/build/outputs/apk/debug/app-debug.apk`.
You
can [Sideload](https://xdaforums.com/t/official-list-of-sideloaded-apps-and-workarounds-for-wear-os-tested-on-galaxy-watch.4379825/)
it onto your Device.