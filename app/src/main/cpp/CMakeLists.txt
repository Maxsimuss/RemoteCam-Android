# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.22.1)

project("RemoteCam")

# Creates your game shared library. The name must be the same as the
# one used for loading in your Kotlin/Java or AndroidManifest.txt files.
file(GLOB RemoteCam_SRC
        "*.cpp"
        )

add_library(RemoteCam SHARED
        ${RemoteCam_SRC}
        )

include_directories(lib)

# Configure libraries CMake uses to link your target library.
#target_link_libraries(RemoteCam)