# ArcContentResolverError
Demonstration of an error with ARC. This app runs correctly on an Android device, but fails on ChromeBook.

This app should be able to load a text file using `ContentResolver`, and retain its `Uri` so that pressing the __Save__ button updates the content of the file with the text on screen. This occurs on Android, but not on ChromeOS when compiled with ARC Welder. 

See this post for further details: http://stackoverflow.com/questions/30192327/using-contentresolver-to-write-to-external-files-chrome-arc
