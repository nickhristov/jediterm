JediTerm
========

[![Build Status](https://travis-ci.org/traff/jediterm.png?branch=master)](https://travis-ci.org/traff/jediterm)

The main purpose of the project is to provide a pure Java terminal widget ready
to embed into IDE. From this comes the name
J(from `Java`)edi(reversed `IDE`)Term(obviously from `terminal`)

Project Contact: Dmitry Trofimov <dmitry.trofimov@jetbrains.com>


License
-------
Licensed under LGPL.


Features
--------

* ssh using JSch from jcraft.org
* vt102 emulation
* xterm colours
* back buffer so selection / cut and paste works
* scroll buffer
* terminal resizing from client or server side


Links
-----
 * Terminal protocol description: http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
 * Terminal Character Set Terminology and Mechanics: http://www.columbia.edu/kermit/k95manual/iso2022.html
 * JPty library: https://github.com/jawi/JPty
 * JSch library: http://www.jcraft.com/jsch
 * UTF8 Demo: http://www.cl.cam.ac.uk/~mgk25/ucs/examples/UTF-8-demo.txt
 * Control sequences visualization: http://www.gnu.org/software/teseq/



Origin
------
Continued as a fork of Gritty (http://code.google.com/p/gritty) by Robert Wittams
and Omer Kudat, which was in its own turn a rework of
JCTerm(http://www.jcraft.com/jcterm) by Atsuhiko Yamanaka
