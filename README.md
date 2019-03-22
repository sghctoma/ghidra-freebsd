# How to train your dragon... to be nice with the daemon

Although most of Ghidra is written in the platform-independent awesomeness that we know as Java, there are some native executables too - most notably the decompiler. Because of this, you can't really use it on FreeBSD, at least, not out of the box. The native executables work with Linuxulator though, which is a good starting point. So let's make Ghidra work with Beastie!

First step, we need to fix the shebangs:

```sh
[0x00 ghidra_9.0]$ grep -R '#!/bin/bash' * | cut -f1 -d: | xargs sed -i "" 's|#!/bin/bash|#!/usr/bin/env bash|g'
```

Moving on. Native executables are placed in directories named after the OS and architecture, like so:

```sh
[0x00 ghidra_9.0]$ find . -name linux64
./Ghidra/Features/Decompiler/os/linux64
./Ghidra/Features/GhidraServer/os/linux64
./GPL/DemanglerGnu/os/linux64
./Extensions/Ghidra/Skeleton/os/linux64
[0x00 ghidra_9.0]$ ls -l ./Ghidra/Features/Decompiler/os/
total 2
drwxr-xr-x  2 sghctoma  sghctoma  4 Mar 22 10:08 linux64
drwxr-xr-x  2 sghctoma  sghctoma  4 Mar 22 10:08 osx64
drwxr-xr-x  2 sghctoma  sghctoma  4 Mar 22 10:08 win64
[0x00 ghidra_9.0]$
```

The `ghidra.framework.Architecture`, `ghidra.framework.OperatingSystem` and `ghidra.framework.Platform` classes decide which directory to use. FreeBSD is not a supported platform, so the native binaries won't be found. But exactly because FreeBSD is not supported, the OS name is `null`, which becomes "null" when concatenated to the path. This means, we can copy the `whatever/os/linux64` directories to `whatever/os/null` for a quick&dirty fix:

```sh
[0x00 ghidra_9.0]$ find . -name linux64 -exec cp -r {} {}/../null \;
```

This works, but it's ugly. More so, Ghidra failed to automatically load external libraries. I did not realize it's a known bug (NationalSecurityAgency/ghidra#110), and suspected the problem could be resolved by patching the `Platform.getAdditionalLibraryPaths` method. So I [patched](patches/platform) it, put the class files in a directory that I added to the classpath. And Ghidra still loaded the original classes even though my directory was the first in the classpath. Then I remembered Ghidra uses its own classloader (`ghidra.GhidraClassLoader`), checked it, and turned out `ghidra.GhidraLauncher` builds the classpath from several sources. I didn't really want to read the whole thing, so I've decided the easiest way to ensure my classes have precedence is if they have no competitors at all:

```sh
[0x00 ghidra_9.0]$ zip -d Ghidra/Framework/Generic/lib/Generic.jar ghidra/framework/Platform.class
[0x00 ghidra_9.0]$ zip -d Ghidra/Framework/Generic/lib/Generic.jar ghidra/framework/Architecture.class
[0x00 ghidra_9.0]$ zip -d Ghidra/Framework/Utility/lib/Utility.jar ghidra/framework/OperatingSystem.class
```

After this, I was finally able to rename those `null` directories to `freebsd64`. Of course the library loading still does not work, but I'll just wait for the NSA to fix that.

One additional note before I finish. We have the source code for the `demangler_gnu` executable, because it uses GPL-d components. It builds on FreeBSD with the attached [patch](patches/demangler), so you don't have to use the Linux version of that. The `./Ghidra/Features/GhidraServer/os/linux64/libjpam.so` is also opensource (gregrluck/jpam), but did not try to build it yet.

So, I guess, that's all. Goodbye, and happy reversing!
