SRC_DIR     := src
LIB_DIR     := lib
OBJ_DIR     := obj
BIN_DIR     := bin
TEST_DIR    := tests
JAVADOC_DIR := docs/javadoc

DESKTOP_MANIFEST := manifest-desktop.mf
WEB_MANIFEST     := web-manifest.mf


.PHONY: compile_desktop \
	compile_web     \
	jar_desktop     \
	jar_web         \
	build_mac       \
	build_linux     \
	build_windos    \
	build_web       \
        test            \
	javadoc         \
	javadoc_dir     \
	obj_dir         \
	bin_dir         \
	rel_dir         \
	clean

compile_desktop: obj_dir
	javac -cp '$(SRC_DIR)/$(LIB_DIR)/*' -d $(OBJ_DIR)               \
		$(shell find src/main -name '*.java' | grep -v web)

compile_web: obj_dir
	javac -cp '$(SRC_DIR)/$(LIB_DIR)/*' -d $(OBJ_DIR)               \
		$(shell find src/main -name '*.java' | grep -v desktop)

jar_desktop: compile_desktop bin_dir
	mkdir -p $(BIN_DIR)/$(LIB_DIR)
	cp -a $(SRC_DIR)/$(LIB_DIR)/* $(BIN_DIR)/$(LIB_DIR)
	jar cmf $(DESKTOP_MANIFEST)             \
		bin/PeriodCountdown-desktop.jar \
		-C $(OBJ_DIR) .                 \
		-C $(SRC_DIR) assets

jar_web: compile_web bin_dir
	mkdir -p $(BIN_DIR)/$(LIB_DIR)
	mkdir -p $(BIN_DIR)/server
	cp -a $(SRC_DIR)/$(LIB_DIR)/* $(BIN_DIR)/$(LIB_DIR)
	cp -a $(SRC_DIR)/main/web/server bin/server
	jar cmf $(DESKTOP_MANIFEST)             \
		bin/PeriodCountdown-desktop.jar \
		-C $(OBJ_DIR) .                 \
		-C $(SRC_DIR) assets

build_mac: jar_desktop rel_dir
	jpackage                                       \
		--name PeriodCountdown                 \
		--app-version $(APP_VERSION)           \
		--input $(BIN_DIR)                     \
		--dest $(BIN_DIR)                      \
		--icon $(SRC_DIR)/assets/icon.icns     \
		--main-jar PeriodCountdown-desktop.jar \
		--main-class desktop.PCDesktopApp      \
		--mac-package-name "Period Countdown"

build_linux: jar_desktop rel_dir
	jpackage                                       \
		--name PeriodCountdown                 \
		--app-version $(APP_VERSION)           \
		--input $(BIN_DIR)                     \
		--dest $(BIN_DIR)                      \
		--icon $(SRC_DIR)/assets/icon.png      \
		--main-jar PeriodCountdown-desktop.jar \
		--main-class desktop.PCDesktopApp

build_windows: jar_desktop rel_dir
	jpackage                                       \
		--name PeriodCountdown                 \
		--app-version $(APP_VERSION)           \
		--input $(BIN_DIR)                     \
		--dest $(BIN_DIR)                      \
		--icon $(SRC_DIR)/assets/icon.ico      \
		--main-jar PeriodCountdown-desktop.jar \
		--main-class desktop.PCDesktopApp

build_web: jar_web rel_dir
	mkdir -p $(BIN_DIR)/PeriodCountdown-$(APP_VERSION)-web
	rsync -r --exclude "*~" $(BIN_DIR)/* $(BIN_DIR)/PeriodCountdown-$(APP_VERSION)-web
	tar -czvf                                                    \
		$(BIN_DIR)/PeriodCountdown-$(APP_VERSION)-web.tar.gz \
		$(BIN_DIR)/PeriodCountdown-$(APP_VERSION)-web/*

test: jar_desktop
	javac -cp '.:$(SRC_DIR)/lib/*:$(BIN_DIR)/*' -d $(OBJ_DIR)/$(TEST_DIR) \
		$(shell find $(TEST_DIR) -name '*.java')
	java -cp '.:$(SRC_DIR)/lib/*:$(OBJ_DIR)/$(TEST_DIR):$(BIN_DIR)/*' \
		org.junit.runner.JUnitCore                                \
		TestOSPath TestUTCTime TestDuration

javadoc: javadoc_dir
	javadoc $(shell find $(SRC_DIR)/main -name "*.java" -not -path "web/*")   \
		-d $(JAVADOC_DIR)                                                 \
		-cp .:src/lib/picocli.jar:src/lib/jnet.jar:src/lib/gson-2.2.2.jar

javadoc_dir:
	mkdir -p $(JAVADOC_DIR)

obj_dir:
	mkdir -p $(OBJ_DIR)

bin_dir:
	mkdir -p $(BIN_DIR)

clean:
	@rm -rf $(BIN_DIR) $(OBJ_DIR) $(JAVADOC_DIR)
