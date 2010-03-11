Introduction
============
DistBoggle is a tool to search for the highest-scoring Boggle board using distributed genetic algorithms. It was created by Ankur Dave in the process of writing his Extended Essay in January 2009 [1].

[1] http://ankurdave.com/ee.html

Source
======
You can get the source of DistBoggle from Mercurial. Run:
	hg clone http://ankurdave.com/hg/distboggle

Compiling
=========
In the root of the project (.../distboggle/), run:
	make

The class files will be generated in bin/.

Running
=======
DistBoggle uses a client-server architecture. To start the server, run
	java com.ankurdave.boggle.ServerTester [SERVER_PORT [DICT_PATH]]

There are multiple kinds of clients. The main one uses genetic algorithms; to start it, run
	java com.ankurdave.boggle.GeneticClientTester [SERVER_IP [SERVER_PORT [DICT_PATH]]]

There is also a hill-climbing client; to start it, run
	java com.ankurdave.boggle.HillClimbClient [SERVER_IP [SERVER_PORT [DICT_PATH]]]

To simply calculate the score of a board, run
	java com.ankurdave.boggle.BoardTester [GRID_PATH [SIDE_LENGTH [DICT_PATH]]]

Here are the defaults for these options:
SERVER_IP: 192.168.1.123
SERVER_PORT: 4444
DICT_PATH: words.txt
GRID_PATH: (empty)
SIDE_LENGTH: 4

When GRID_PATH is empty, a random board is generated. When it is "-" (a single hyphen), it is read from stdin. Rows should be separated by newlines.

Statistics
==========
Some statistics used in the Extended Essay are stored in the stats/ directory, as *.dat files. Use calculate-stats.pl to process these and extract useful information. Run
	perl calculate-stats.pl foo.dat

License
=======
DistBoggle is licensed under the terms of the GNU General Public License. See LICENSE.txt for the full license text.

