package org.move.cli.sdks

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.system.CpuArch
import java.io.File

data class AptosSdk(val sdksDir: String, val version: String) {
    val githubArchiveUrl: String
        get() {
            return "https://github.com/aptos-labs/aptos-core/releases/download" +
                    "/aptos-cli-v$version/$githubArchiveFileName"
        }

    val githubArchiveFileName: String
        get() {
            val os = PlatformOS.current().title
            return "aptos-cli-$version-$os-x86_64.zip"
        }

    val targetFile: File
        get() = File(sdksDir, if (SystemInfo.isWindows) "aptos-$version.exe" else "aptos-$version")
}

enum class PlatformOS(val title: String) {
    Windows("Windows"),
    MacOS("MacOSX"),
    LinuxX86("Linux"),
    LinuxArm64("Linux-Arm");

    companion object {
        fun current(): PlatformOS {
            return when {
                SystemInfo.isMac -> MacOS
                SystemInfo.isWindows -> Windows
                CpuArch.isArm64() -> LinuxArm64
                else -> LinuxX86
            }
        }
    }
}
