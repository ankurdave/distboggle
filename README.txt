Introduction
============
DistBoggle is a tool to search for the highest-scoring Boggle board using distributed genetic algorithms. It was created by Ankur Dave in the process of writing his Extended Essay in January 2009 [1].

[1] http://ankurdave.com/ee.html

Source
======
You can get the source of DistBoggle from Subversion. Run:
	svn co http://ankurdave.com/svn/ankur/programming/distboggle
to get the source. You don't need a username or password to download the source.

Compiling
=========
In the root of the project (.../distboggle/), run:
	javac com/ankurdave/boggle/*.java

Running
=======
DistBoggle uses a client-server architecture. To start the server, run
	java com.ankurdave.boggle.ServerTester
and the server will listen on port 4444.

There are multiple kinds of clients. The main one uses genetic algorithms; to start it, run
	java com.ankurdave.boggle.GeneticClientTester
and the client will try to connect to a server at 192.168.1.123:4444. To change this, edit GeneticClientTester.java and recompile.

There is also a hill-climbing client; to start it, run
	java com.ankurdave.boggle.HillClimbClient
and the client will try to connect to a server at 192.168.1.123:4444. To change this, edit HillClimbClient.java and recompile.

Finally, to simply calculate the score of a board, run
	java com.ankurdave.boggle.BoardTester words.txt 4 -
and enter the board, one line at a time, pressing Enter after each line.

Statistics
==========
Some statistics used in the Extended Essay are stored in the stats/ directory, as *.dat files. Use calculate-stats.pl to process these and extract useful information. Run
	perl calculate-stats.pl foo.dat

License
=======
DistBoggle is licensed under the terms of the GNU General Public License. See LICENSE.txt for the full license text.

