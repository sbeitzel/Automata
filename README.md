Automata Explorer
=================

Automata Explorer was a weekend project of mine after a brief but
passionate interest in cellular automata back in 2003. This project
should therefore _not_ be looked at as an example of my most recent
(or best) work.  Nevertheless, I am preserving it here on Github for
historical interest and pure vanity!

These days, if you're interested in a much, much better Cellular
Automata explorer, I would recommend Golly (http://golly.sourceforge.net/)

Building Automata
-----------------

Automata uses the Ant Java build system. To build a JAR file, just type:

    % ant


To build a full release, including a versioned ZIP file, type:

    % ant release

Building on OS X
----------------

On OS X, this project uses the JarBundler ant task (another weekend
project of mine) to create an executable Java bundle. Type:

    % ant release-macosx


Running Automata Explorer
-------------------------

Automata requires the Java Runtime version 1.3 or later.

To run by hand from the command line, just type

UNIX or OS X:

    % java -jar bin/automata.jar

WINDOWS

    > java -jar BIN\automata.jar


Mac OS X releases will produce a double-clickable bundle, as well.


License
-------

Copyright (c) 2012, Seth J. Morabito <sethm@loomcom.com> All rights reserved.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See  the GNU General Public License for
more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA  02111-1307, USA.
