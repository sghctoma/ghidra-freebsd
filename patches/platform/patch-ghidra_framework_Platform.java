--- ghidra/framework/Platform.java.orig	2019-03-22 20:40:47.493117000 +0100
+++ ghidra/framework/Platform.java	2019-03-22 20:41:19.900424000 +0100
@@ -71,6 +71,16 @@
 	MAC_UNKNOWN(OperatingSystem.MAC_OS_X, Architecture.UNKNOWN, "osx64", ".dylib", ""),
 
 	/**
+	 * Identifies a FreeBSD OS.
+	 */
+	FREEBSD_32(OperatingSystem.FREEBSD, Architecture.X86, "freebsd32", ".so", ""),
+
+	/**
+	 * Identifies a FreeBSD OS.
+	 */
+	FREEBSD_64(OperatingSystem.FREEBSD, Architecture.X86_64, "freebsd64", ".so", ""),
+
+	/**
 	 * Identifies an unsupported OS.
 	 */
 	UNSUPPORTED(OperatingSystem.UNSUPPORTED, Architecture.UNKNOWN, null, null, "");
@@ -144,6 +154,15 @@
 			paths.add("/usr/lib");
 			paths.add("/usr/X11R6/bin");
 			paths.add("/usr/X11R6/lib");
+		}
+		else if (operatingSystem == OperatingSystem.FREEBSD) {
+			paths.add("/bin");
+			paths.add("/lib");
+			paths.add("/usr/bin");
+			paths.add("/usr/lib");
+			paths.add("/usr/local/bin");
+			paths.add("/usr/local/lib");
+			paths.add("/usr/local/lib/compat");
 		}
 		else if (CURRENT_PLATFORM == WIN_64) {
 			String windir = System.getenv("SystemRoot");
