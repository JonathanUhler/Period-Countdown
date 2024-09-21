# Period-Countdown
A productivity application to display time remaining until a class based on a school schedule.

# Installation
Period Countdown can be used through the website, [periodcountdown.net](https://periodcountdown.net)
or through a native desktop application. The desktop app requires Java 17 or later.

## Building a Package File
Package files can be built and installed for Mac OS, Linux, and Windows. Follow the corresponding
guide below for your operating system:

### Mac OS
1) Clone or download the repository from GitHub.
2) Run `make build_mac` to build the DMG file.
3) Double-click on the "PeriodCountdown-X.X.X.dmg" file in the bin directory to launch the
   installer. A new window with "PeriodCountdown.app" should appear. Drag the .app file to the
   /Applications folder or another location.

## Linux
1) Clone or download the repository from GitHub.
2) Run `make build_linux` to build the DEB file.
3) Double-click on the "PeriodCountdown\_X.X.X-1\_amd64.deb" file in the bin directory to launch
   the Linux software manager. Click "Install". Alternatively, install through apt with
   `sudo apt install ./PeriodCountdown_x.x.x-1_amd64.deb`.

The launch process is still not completely smooth but does work. To start the app use these steps:
1) If installed with the software manager, figure out where the app was installed by going to the
   software manager > "Installed" > "PeriodCountdown"
2) If installed with apt, the software is likely at `/opt/PeriodCountdown/`
3) Run the executable with `/path/to/periodcountdown/bin/PeriodCountdown`
   1) Ex: `/opt/periodcountdown/bin/PeriodCountdown`

## Windows
1) Clone or download the repository from GitHub.
2) Run `make build_windows` to build the EXE file.
3) Double-click on the "PeriodCountdown-X.X>X.exe" file in the bin directory to launch the
   installer.

In order to allow Period-Countdown to be found from the search menu, do the following:
1) Open File Explorer and go to "C:\\Program Files\\PeriodCountdown".
2) Right-click on the PeriodCountdown exactuable and select "Create Shortcut".
3) The shortcut will be created on your desktop. Move it to
   "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs".
