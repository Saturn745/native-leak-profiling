# native-leak-profiling
A simple repository where I make random docker images that make native memory leaks more bearable to debug in Java applications.

These docker images are mainly intended for Minecraft servers, for use in Pterodactyl installations.\
These images use Jemalloc to create small heapdumps and converts them into readable GIFs (via jeprof).

You can read more about native memory leaks, how to debug them, and how to read Jeprof GIFs [here](https://github.com/jeffgriffith/native-jvm-leaks/blob/master/README.md).

## Images
Eclipse Temurin Java: `ghcr.io/skullians/native-leak-profiling:java_REPLACE_ME` (Supported: 24, 21, 17, 11, 8)

## Usage

### Automatic Dumping
Once you have set the custom docker image, let the server run and allow Jeprof GIFs to accumulate in `dumps/output`.\
By default, Jemalloc is configured to make dumps every 2GiB of memory allocation - these dumps will not freeze your server, and they are only 50-200KiB in size.\
However, the Docker images will automatically remove these and convert them into readable GIFs, each of which are around 200-300KiB. Plan accordingly for increased storage usage if you plan to run it for a long time.\
You can then analyze these GIFs once created - you will have a lot to go through - (see https://github.com/jeffgriffith/native-jvm-leaks/blob/master/README.md)

### Thread Dumping
This docker image also automatically takes and analyzes thread dumps for certain keywords.\
You must enable this with the `-Danalyse=true` flag.\
You must also specify the following flags:
- `-Dinterval=60` - This is the interval in seconds that thread dumps will be taken. Realistically, the lower the better, as you have a better chance of taking a thread dump when the native method is being called from the JVM.
- `-Dkeyword=keyword` - This is the keyword that should be checked for in the thread dump. **If this matches, the thread dump will be copied to `dumps/traces`.

Don't get me wrong - this isn't as easy as it sounds - these Jemalloc dumps and Jeprof GIFs **will not tell you what is causing the native leak directly**. For example, we had a chat plugin that didn't close zip inflators, which caused our leak. We only knew it was this plugin when we removed the plugin.\
Chances are, you'll have to do something similar, which may prove difficult - ideally try to check src core of plugins / mods, although this may prove difficult for paid resources.

### Key points for reading generated GIFs
- Data points that link to `je_malloc_default` **without** passibg through `os#malloc` will likely be a native memory leak. These data points do not pass directly through the JVM collector thus cannot be GC'd.

## Why?
I started working on a Jemalloc native memory profiling image once encountered significant native memory leaks on our Velocity proxy.

At some points we were hitting 40gb heap usage.

As a result, we had to move out of Pterodactyl + Docker, and run our proxies in SSH temporarily with Jemalloc enabled to profile the leak...
This is because you must supply jeprof with the java binary used to make the dumps, so people can't SSH in manually and run jeprof there, otherwise you get an output GIF that is a garbled mess.

In our case, it was a native leak as a result of ZIP inflators not being AutoCloseable - one of our plugins made use of these for regular plugin messages, resulting in a very quick native leak.

I wanted to plan for the future, in case I experienced a similar issue, thus here we are.

## Acknowledgements
These are adapted Dockerfiles and entrypoints from [pterodactyl/yolks](https://github.com/pterodactyl/yolks/tree/master/java).
