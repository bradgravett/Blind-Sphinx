Blind Sphinx is software to automatically resolve matchups for a variant of Magic: The Gathering, called 3 Card Blind.
Blind Sphinx relies on Forge for rules adjudication, found at https://github.com/Card-Forge/forge. Forge is included as
a git submodule at [/forge](./forge), built with Maven, and consumed from the local Maven repository.

This is a Kotlin Multiplatform project targeting Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that's common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple's CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

### Prerequisites

- JDK 17 or newer
- Maven (`brew install maven` on macOS)

Clone with the Forge submodule, then build and install Forge into `~/.m2`. Re-run `installForge` after updating the
submodule; it is skipped when Forge is already installed for the current submodule commit.
```shell
git clone --recursive https://github.com/bradgravett/Blind-Sphinx.git
cd Blind-Sphinx
./gradlew installForge
```

Already cloned without `--recursive`? Run `git submodule update --init` first.

To move to Forge's newest release (`forge-X.Y.Z` tag), run `./gradlew updateForge`, then `./gradlew installForge`, and
commit the updated `forge` submodule.

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

---

### License

Blind Sphinx is licensed under the [GNU General Public License v3.0](LICENSE).

Blind Sphinx depends on [Forge](https://github.com/Card-Forge/forge), which is also GPL-3.0. Card data sourced
from Forge's resource files is subject to [Wizards of the Coast's Fan Content Policy](https://company.wizards.com/en/legal/fancontentpolicy)
and is for non-commercial use only.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
