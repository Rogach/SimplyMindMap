Simply Mind Map
===============

I took [FreeMind](http://freemind.sourceforge.net/wiki/index.php/Main_Page)
sorce code, removed lots of heavyweight features and created simple standalone
mind map component, that can be easily embedded in any existing java swing gui.

Project was developed in NetBeans, using Ant as a build system,
so you can just use `ant run` to see quick demo.

`org.rogach.simplymindmap.MindMap` extends JPanel, thus you only need
to initialize it and add it to some component - no heavy-weight initialization
is needed.
