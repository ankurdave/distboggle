SOURCE_FILES= \
com/ankurdave/boggle/Board.java \
com/ankurdave/boggle/BoardDimensionMismatchException.java \
com/ankurdave/boggle/BoardPerformanceTester.java \
com/ankurdave/boggle/BoardTester.java \
com/ankurdave/boggle/Dictionary.java \
com/ankurdave/boggle/DictionaryTester.java \
com/ankurdave/boggle/GenerationEmptyException.java \
com/ankurdave/boggle/GeneticBoard.java \
com/ankurdave/boggle/GeneticClient.java \
com/ankurdave/boggle/GeneticClientTester.java \
com/ankurdave/boggle/GeneticClientThread.java \
com/ankurdave/boggle/HillClimbClient.java \
com/ankurdave/boggle/HillClimber.java \
com/ankurdave/boggle/MutatingBoard.java \
com/ankurdave/boggle/Population.java \
com/ankurdave/boggle/PopulationTester.java \
com/ankurdave/boggle/Server.java \
com/ankurdave/boggle/ServerTester.java \
com/ankurdave/boggle/ServerThread.java \
com/ankurdave/boggle/TrieNode.java \
com/ankurdave/boggle/Util.java

OBJECT_DIR=bin

OBJECT_FILES=$(addprefix $(OBJECT_DIR)/,$(patsubst %.java,%.class,$(SOURCE_FILES)))

PACKAGE=distboggle.zip

PACKAGE_FILES=LICENSE.txt README.txt words.txt

.PHONY: all clean

all: $(OBJECT_FILES) $(PACKAGE)

$(OBJECT_DIR):
	mkdir -p $@

$(OBJECT_DIR)/%.class: %.java $(OBJECT_DIR)
	javac $< -d $(OBJECT_DIR)

$(addprefix $(OBJECT_DIR)/,$(PACKAGE_FILES)): $(OBJECT_DIR)/%: %
	cp $< $@

clean:
	rm -r $(OBJECT_DIR)

$(PACKAGE): $(OBJECT_DIR) $(OBJECT_FILES) $(addprefix $(OBJECT_DIR)/,$(PACKAGE_FILES))
	cd $(OBJECT_DIR); zip -r $@ .; mv $@ ..