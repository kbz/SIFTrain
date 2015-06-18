# SIFTrain
An android rhythm game based on Love Live School Idol Festival in combination with elements from osu!

Written in JAVA, using the [libgdx library](https://github.com/libgdx/libgdx)

# Known Issues

Because of the way multitouch is supported, it is not possible to identify in 
a reliable way which pointer has been lifted, which is why, currently, if you
press a note, slide the finger off the "tap zone", the note will remain in
pressed state. In a previous implementation, it was found out that the pointer
may reset if multiple touch events happen while one of the pointers is already
in use.